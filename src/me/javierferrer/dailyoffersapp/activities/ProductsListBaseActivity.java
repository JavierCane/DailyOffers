package me.javierferrer.dailyoffersapp.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.Product;
import me.javierferrer.dailyoffersapp.models.ProductsList;
import me.javierferrer.dailyoffersapp.utils.ProductsAdapter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class ProductsListBaseActivity extends SherlockActivity
{

	protected static final List<String> CATEGORIES = new ArrayList<String>( asList( "Wines", "Spirits", "Beers" ) );
	protected static ProductsListBaseActivity sProductsListBaseActivity;
	protected static ProductsAdapter sCurrentCategoryProductsAdapter = null;
	protected static ActionBar sActionBar;

	protected static final String mClassName = "ProductsListBaseActivity";
	protected final List<String> mVisibleCategories = new ArrayList<String>();
	protected ListView mProductsListView;
	protected SearchView mSearchView;
	protected MenuItem mSearchMenuItem;

	/**
	 * Boolean to indicate if the categories visibility has changed from the preferences activity or not.
	 * If it's set to true, when we re-create the activity, we'll redraw the categories tabs
	 */
	protected static Boolean sCategoriesVisibilityChanged = false;

	public static final String TAG = "DO";

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		// Action Bar Sherlock compatibility
		super.onCreate( savedInstanceState );

		Log.d( TAG, mClassName + "\t" + "onCreate" );

		initActivity();

		// Ensure that the products list has been loaded and if not, try to load it expecting a callback to the
		// ProductsByCategoryActivity.productsParseCallback() method
		try
		{
			ProductsList.getInstance()
					.loadFavoritedProducts( getApplicationContext().openFileInput( ProductsList.FAVORITES_FILE_NAME ) );
		}
		catch ( FileNotFoundException e )
		{
			Log.d( ProductsListBaseActivity.TAG, mClassName + "\t\t\t\t" +
			                                     "onCreate: FileNotFoundException (Probably the user has not defined any favorited product yet)." );
		}

		Log.d( TAG, mClassName + "\t" + "onCreate: ProductsList isLoaded: " + ProductsList.getInstance().isLoaded() );

		// If we don't loaded the products JSON yet, do it in the background
		if ( !ProductsList.getInstance().isLoaded() )
		{
			ProductsList.getInstance().execute( getResources().openRawResource( R.raw.products ) );
		}

		setNavigationWithoutTabs();

		// Call to the handleIntent method. It has to be implemented in the class child's
		if ( getIntent().getAction() != null )
		{
			Log.d( TAG, mClassName + "\t" + "onCreate: Ready to handle the intent" );
			handleIntent( getIntent() );
			Log.d( TAG, mClassName + "\t" + "onCreate: Intent handled" );
		}

		// Set products list listeners
		setListListeners();
	}

	/**
	 * On start method overridden in order to re-initialize tabs when preferences has been changed
	 * This is because the user could be set as hidden/shown some categories
	 */
	@Override
	public void onStart()
	{
		super.onStart();

		Log.d( TAG, mClassName + "\t" + "onStart" );

		// If the user has changed the visible categories preferences, re-initialize the visible categories
		if ( sCategoriesVisibilityChanged )
		{
			initVisibleCategories();

			setCategoriesVisibilityChanged( false );
		}
	}

	/**
	 * Initialize the current activity setting the layout and the class attributes
	 */
	private void initActivity()
	{
		// Set layout
		setContentView( R.layout.products_list );

		// Initialize class attributes
		sProductsListBaseActivity = this;
		mProductsListView = ( ListView ) findViewById( R.id.products_list );
		sActionBar = getSupportActionBar();
		initVisibleCategories();
	}

	protected void handleIntent( Intent intent )
	{

	}

	public static void setCategoriesVisibilityChanged( Boolean status )
	{
		sCategoriesVisibilityChanged = status;
	}

	/******************************************************************************************************
	 * Products list
	 *****************************************************************************************************/

	/**
	 * Set items click listener
	 */
	private void setListListeners()
	{
		// List view long click context menu
		registerForContextMenu( mProductsListView );

		// Product click details activity)
		mProductsListView.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				Product selectedProduct = ( Product ) mProductsListView.getItemAtPosition( position );

				Log.i( TAG, mClassName + "\t" + "setListListeners: click on: " + selectedProduct.getName() );

				Intent productDetailsIntent = new Intent( ProductsListBaseActivity.this, ProductDetailsActivity.class );

				productDetailsIntent.putExtra( "Product", selectedProduct );

				ProductsListBaseActivity.this.startActivity( productDetailsIntent );
			}
		} );
	}

	/**
	 * Context menu for list items (view products details and buy product options)
	 *
	 * @param menu
	 * @param view
	 * @param menuInfo
	 */
	@Override
	public void onCreateContextMenu( ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo )
	{
		// Inflate the context menu with the appropriate menu resource
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.products_list_menu, menu );

		// Get the context menu info
		AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) menuInfo;

		// Get selected product
		Product selectedProduct = ( Product ) mProductsListView.getItemAtPosition( info.position );

		// Set the context menu title to the product name
		menu.setHeaderTitle( selectedProduct.getName() );

		// If the selected product is currently favorited, change the menu item title from "Bookmark this product" to "Remove from bookmarks"
		if ( selectedProduct.isFavorited() )
		{
			menu.findItem( R.id.mi_favorite_product ).setTitle( getResources().getString( R.string.remove_favorite ) );
		}
	}

	/**
	 * Context menu item selected (view details and buy options)
	 *
	 * @param item
	 * @return
	 */
	@Override
	public boolean onContextItemSelected( android.view.MenuItem item )
	{
		AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) item.getMenuInfo();

		Product selectedProduct = ( Product ) mProductsListView.getItemAtPosition( info.position );

		switch ( item.getItemId() )
		{
			// In case of view product details menu item click, create an intent to the product details activity
			case R.id.mi_view_details:
				Intent productDetailsIntent = new Intent( ProductsListBaseActivity.this, ProductDetailsActivity.class );

				productDetailsIntent.putExtra( "Product", selectedProduct );

				startActivity( productDetailsIntent );

				return true;
			// In case of buy product menu item click, create an intent to redirect to the product buy site
			case R.id.mi_buy:
				Intent intent = new Intent( this.getBaseContext(), WebViewActivity.class );

				Bundle bundle = new Bundle();
				bundle.putString( "url", selectedProduct.getBuyUrl() );
				intent.putExtras( bundle );

				startActivity( intent );

				return true;
			// In case of add/remove from bookmarks menu item click
			case R.id.mi_favorite_product:
				setFavoriteProduct( selectedProduct, !selectedProduct.isFavorited() );

				return true;
			default:
				return super.onContextItemSelected( item );
		}
	}

	/******************************************************************************************************
	 * Categories & tabs
	 *****************************************************************************************************/

	/**
	 * Add to the mVisibleCategories array the categories set as visible by the user in the settings
	 */
	protected void initVisibleCategories()
	{
		Log.d( TAG, mClassName + "\t" + "initVisibleCategories" );

		if ( !mVisibleCategories.isEmpty() )
		{
			mVisibleCategories.clear();
		}

		// Get the xml/preferences.xml preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );

		for ( String categoryName : CATEGORIES )
		{
			if ( preferences.getBoolean( categoryName.toLowerCase() + "_visible", true ) )
			{
				mVisibleCategories.add( categoryName );
			}
		}

		// If the used has not set any category visible in settings, inform about it
		if ( mVisibleCategories.isEmpty() )
		{
			Toast.makeText( getApplicationContext(), getResources().getString( R.string.no_visible_categories ),
					Toast.LENGTH_LONG ).show();
		}
	}

	/**
	 * Set navigation mode tabs in order to show the constructed tabs
	 */
	protected void setNavigationWithTabs()
	{
		Log.d( TAG, mClassName + "\t" + "setNavigationWithTabs" );

		sActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

		sActionBar.setHomeButtonEnabled( false );
		sActionBar.setDisplayHomeAsUpEnabled( false );
	}

	/**
	 * Hide tabs changing the navigation mode to standard
	 */
	protected void setNavigationWithoutTabs()
	{
		Log.d( TAG, mClassName + "\t" + "setNavigationWithoutTabs" );

		sActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );

		sActionBar.setHomeButtonEnabled( true );
		sActionBar.setDisplayHomeAsUpEnabled( true );
	}

	/******************************************************************************************************
	 * Action Bar (search, bookmarks and settings)
	 *****************************************************************************************************/

	/**
	 * Creates Action Bar menus items (search and settings)
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getSupportMenuInflater().inflate( R.menu.products_list_action_bar_menu, menu );

		mSearchMenuItem = menu.findItem( R.id.mi_search ); // Get search menu item

		// Associate searchable configuration with the SearchView (res/xml/searchable.xml)
		SearchManager searchManager = ( SearchManager ) getSystemService( Context.SEARCH_SERVICE );
		mSearchView = ( SearchView ) menu.findItem( R.id.mi_search ).getActionView();
		mSearchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );

		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		Log.d( TAG, mClassName + "\t" + "onOptionsItemSelected: " + item.getTitle() );

		switch ( item.getItemId() )
		{
			case android.R.id.home:
				Intent intent = new Intent( this, ProductsByCategoryActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );
				return true;
			case R.id.mi_favorites_list:
				startActivity( new Intent( getBaseContext(), FavoritedProductsActivity.class ) );
				return true;
			case R.id.mi_settings:
				startActivity( new Intent( getBaseContext(), PreferencesActivity.class ) );
				return true;
			default:
				return super.onOptionsItemSelected( item );
		}
	}

	public static ProductsAdapter getFavoritedProductsAdapter()
	{
		return sCurrentCategoryProductsAdapter;
	}

	public static void setFavoriteProduct( Product selectedProduct, boolean newStatus )
	{
		Log.d( TAG, mClassName + "\t" + "setFavoriteProduct: " + selectedProduct.getName() + ", to: " + newStatus );

		// Notify to the user
		String notificationText;
		if ( newStatus )
		{
			notificationText = String.format(
					sProductsListBaseActivity.getResources().getString( R.string.favorited_product_added_confirm ),
					selectedProduct.getName() );
		}
		else
		{
			notificationText = String.format(
					sProductsListBaseActivity.getResources().getString( R.string.favorited_product_removed_confirm ),
					selectedProduct.getName() );
		}

		Toast.makeText( sProductsListBaseActivity.getApplicationContext(), notificationText, Toast.LENGTH_SHORT )
				.show();

		// Set the product favorited flag to the new status
		selectedProduct.setFavorited( newStatus );

		// Add/remove the selected product to the favorited products list
		ProductsList.getInstance()
				.setFavoritedProduct( sProductsListBaseActivity.getApplicationContext(), selectedProduct.getId(),
						newStatus );

		// Add/remove it from the favorited products adapter
		FavoritedProductsActivity.setFavoriteProduct( selectedProduct, newStatus );
	}
}
