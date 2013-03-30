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
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.Product;
import me.javierferrer.dailyoffersapp.models.ProductsList;
import me.javierferrer.dailyoffersapp.utils.ProductsAdapter;
import me.javierferrer.dailyoffersapp.widgets.ProductsSearchView;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class ProductsListBaseActivity extends SherlockActivity
{

	protected static final List<String> sAllCategories = new ArrayList<String>( asList( "Wines", "Spirits", "Beers" ) );
	protected static ProductsListBaseActivity sProductsListBaseActivity;
	protected static ListView sProductsListView;
	protected static ActionBar sActionBar;

	protected final List<String> mVisibleCategories = new ArrayList<String>();
	protected ProductsSearchView mSearchView;

	public static final String TAG = "DO";

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		Log.d( TAG, "ProductsListBaseActivity: onCreate" );

		// Action Bar Sherlock compatibility
		super.onCreate( savedInstanceState );

		// Set layout
		setContentView( R.layout.products_list );

		// Calls made in order to execute onCreateContextMenu() method (its initialize mSearchView attribute) before the handleIntent()
//		openOptionsMenu();
//		closeOptionsMenu();

		sProductsListBaseActivity = this;
		sProductsListView = ( ListView ) findViewById( R.id.products_list );
		sActionBar = getSupportActionBar();

		// Firstly we need to ensure that the products list has been loaded and if not, try to load it!
		ProductsList.getInstance().ensureLoaded( getResources(), this.getApplicationContext() );

		Log.d( TAG, "ProductsListBaseActivity: onCreate: Ready to handle the intent" );

		handleIntent( getIntent() );

		Log.d( TAG, "ProductsListBaseActivity: onCreate: Intent handled" );

		// Set products list listeners
		registerForContextMenu( sProductsListView ); // Set the list view long clickable and responding with a context menu
		setListListeners();

		Log.d( TAG, "ProductsListBaseActivity: onCreate: Listeners set" );
	}

	/**
	 * On start method overrided in order to re-initialize tabs when preferences has been changed
	 * This is because the user could be set as hidden/shown some categories
	 */
	@Override
	public void onStart()
	{
		Log.d( TAG, "ProductsListBaseActivity: onStart" );

		if ( sProductsListBaseActivity == null )
		{
			Log.e( TAG, "ProductsListBaseActivity: onStart: sProductsListBaseActivity is null" );
		}
		super.onStart();

		// Get mVisibleCategories
		initVisibleCategories();
	}

	@Override
	protected void onNewIntent( Intent intent )
	{
		Log.d( TAG, "ProductsListBaseActivity: onNewIntent: " + intent.toString() );

		setIntent( intent );
		handleIntent( intent );
	}

	protected void preHandleIntent()
	{
		Log.d( TAG, "ProductsListBaseActivity: preHandleIntent" );

		// Get mVisibleCategories
		initVisibleCategories();
	}

	protected abstract void handleIntent( Intent intent );

	/******************************************************************************************************
	 * Product list
	 *****************************************************************************************************/

	/**
	 * Set items click listener
	 */
	private void setListListeners()
	{
		sProductsListView.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				Product selectedProduct = ( Product ) sProductsListView.getItemAtPosition( position );

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
		// TODO: Revisar si en otras pantallas sale el menú o no
		//if ( view.getId() == sProductsListView.getId() )

		// Inflate the context menu with the appropiate menu resource
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.products_list_menu, menu );

		// Get the context menu info
		AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) menuInfo;

		// Get selected product
		Product selectedProduct = ( Product ) sProductsListView.getItemAtPosition( info.position );

		// Set the context menu title to the product name
		menu.setHeaderTitle( selectedProduct.getName() );

		// If the selected product is currently bookmarked, change the menu item title from "Bookmark this product" to "Remove from bookmarks"
		if ( selectedProduct.isBookmarked() )
		{
			menu.findItem( R.id.mi_bookmark_product ).setTitle( getResources().getString( R.string.remove_bookmark ) );
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

		Product selectedProduct = ( Product ) sProductsListView.getItemAtPosition( info.position );

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
			case R.id.mi_bookmark_product:
				// Set the product bookmarked flag to the opposite of the current value
				selectedProduct.setBookmarked( !selectedProduct.isBookmarked() );

				// Add/remove the selected product to the bookmarked products list
				ProductsList.getInstance().setBookmarkedProduct( this.getApplicationContext(), selectedProduct.getId(), selectedProduct.isBookmarked() );

				return true;
			default:
				return super.onContextItemSelected( item );
		}
	}

	/******************************************************************************************************
	 * Categories & tabs
	 *****************************************************************************************************/

	/**
	 * Add to the mVisibleCategories array the current context mVisibleCategories
	 */
	private void initVisibleCategories()
	{
		Log.d( TAG, "ProductsListBaseActivity: initVisibleCategories" );

		mVisibleCategories.clear();

		// Get the xml/preferences.xml preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );

		for ( String categoryName : sAllCategories )
		{
			if ( preferences.getBoolean( categoryName.toLowerCase() + "_visible", true ) )
			{
				mVisibleCategories.add( categoryName );
			}
		}

		// If the used has not set any category visible in settings, inform about it
		if ( mVisibleCategories.isEmpty() )
		{
			Toast.makeText( getApplicationContext(), getResources().getString( R.string.no_visible_categories ), Toast.LENGTH_LONG ).show();
			sProductsListView.setVisibility( ListView.INVISIBLE );
		}
	}

	/**
	 * Set navigation mode tabs in order to show the constructed tabs
	 * Also called by ProductsSearchView in onActionViewCollapsed event trigger
	 */
	public void showTabs()
	{
		Log.d( TAG, "ProductsListBaseActivity: showTabs" );

		sActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

		sActionBar.setHomeButtonEnabled( false );
		sActionBar.setDisplayHomeAsUpEnabled( false );

		sActionBar.setTitle( sProductsListBaseActivity.getResources().getString( R.string.app_name ) );
	}

	/**
	 * Hide tabs changing the navigation mode to standard
	 */
	public void hideTabs()
	{
		Log.d( TAG, "ProductsListBaseActivity: hideTabs" );

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

		// Associate searchable configuration with the SearchView (res/xml/searchable.xml)
		SearchManager searchManager = ( SearchManager ) getSystemService( Context.SEARCH_SERVICE );
		mSearchView = ( ProductsSearchView ) menu.findItem( R.id.mi_search ).getActionView();
		mSearchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );

		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		Log.d( TAG, "ProductsListBaseActivity: onOptionsItemSelected: " + item.getTitle() );

		switch ( item.getItemId() )
		{
//			case android.R.id.home:
//				showTabs();
//				return true;
//			case R.id.mi_search:
//				hideTabs();
//				sProductsListView.setVisibility( ListView.INVISIBLE );
//				return true;
			case R.id.mi_bookmarks_list:
				showBookmarksList();
				return true;
			case R.id.mi_settings:
				Intent settingsActivity = new Intent( getBaseContext(), PreferencesActivity.class );
				startActivity( settingsActivity );
				return true;
			default:
				return super.onOptionsItemSelected( item );
		}
	}

	protected void showBookmarksList()
	{
		hideTabs();

		if ( ProductsList.getInstance().isLoaded() )
		{
			ProductsAdapter productsAdapter = new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getBookmarkedProducts() );
			sProductsListView.setAdapter( productsAdapter );
			sProductsListView.setVisibility( ListView.VISIBLE );
		}
	}
}
