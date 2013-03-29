package me.javierferrer.dailyoffersapp.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import static com.actionbarsherlock.app.ActionBar.Tab;
import static java.util.Arrays.asList;

public class ProductsListActivity extends SherlockActivity implements ActionBar.TabListener
{

	private static Tab sTab;
	private static FragmentTransaction sTransaction;
	private static ListView sProductsListView;
	private static ActionBar sActionBar;
	private static LayoutInflater sLayoutInflater;
	private static ProductsListActivity sProductsListActivity;

	private static final List<String> sAllCategories = new ArrayList<String>( asList( "Wines", "Spirits", "Beers" ) );
	private static final List<String> sVisibleCategories = new ArrayList<String>();

	public static final String TAG = "DO";

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		Log.d( TAG, "ProductsListActivity: onCreate" );

		// Action Bar Sherlock compatibility
		super.onCreate( savedInstanceState );

		// Firstly we need to ensure that the products list has been loaded and if not, try to load it!
		ProductsList.getInstance().ensureLoaded( getResources(), this.getApplicationContext() );

		// Layout
		setContentView( R.layout.products_list );

		// Configure the Action Bar
		sActionBar = getSupportActionBar();
		sProductsListView = ( ListView ) findViewById( R.id.products_list );
		sLayoutInflater = this.getLayoutInflater();
		sProductsListActivity = this;

		Log.d( TAG, "ProductsListActivity: onCreate: Ready to handle the intent" );

		// Get sVisibleCategories
		initCategories();

		// Construct tabs
		initTabs();

		// Handle possible search intent
		handleIntent( getIntent() );

		Log.d( TAG, "ProductsListActivity: onCreate: Intent handled" );

		// Set products list listeners
		registerForContextMenu( sProductsListView ); // Set the list view long clickable and responding with a context menu
		setListListeners();

		Log.d( TAG, "ProductsListActivity: onCreate: Listeners set" );
	}

	/**
	 * On start method overrided in order to re-initialize tabs when preferences has been changed
	 * This is because the user could be set as hidden/shown some categories
	 */
	@Override
	public void onStart()
	{
		super.onStart();

		// Get sVisibleCategories
		initCategories();

		// Construct tabs
		initTabs();
	}

	@Override
	protected void onNewIntent( Intent intent )
	{
		Log.d( TAG, "ProductsListActivity: onNewIntent: " + intent.toString() );

		handleIntent( intent );
	}

	private void handleIntent( Intent intent )
	{
		Log.d( TAG, "ProductsListActivity: handleIntent: " + intent.toString() );

		if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) )
		{
			hideTabs();

			String query = intent.getStringExtra( SearchManager.QUERY );

			sActionBar.setTitle( getResources().getString( R.string.search ) + ": \"" + query + "\"" );

			Log.d( TAG, "ProductsListActivity: handleIntent: search intent, query: " + query );

			// Get the xml/preferences.xml preferences
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );

			// Check if the user has specified that the categories filters affects on search results
			ProductsAdapter productsAdapter;
			if ( preferences.getBoolean( "visibility_affects_search", true ) )
			{
				productsAdapter = new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getFilteredProducts( query, sVisibleCategories ) );
			}
			else
			{
				productsAdapter = new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getFilteredProducts( query, sAllCategories ) );
			}

			sProductsListView.setAdapter( productsAdapter );
			sProductsListView.setVisibility( ListView.VISIBLE );
//			mTextView.setText(getString(R.string.search_results, query));
//			mList.setOnItemClickListener(wordAdapter);
		}
		else // if ( Intent.ACTION_MAIN.equals( intent.getAction() ) )
		{
			Log.d( TAG, "ProductsListActivity: handleIntent: main action intent detected" );

			// Set tabs navigation mode
			showTabs();

			Log.d( TAG, "ProductsListActivity: handleIntent: sProductsListView: " + sProductsListView.toString() );

			if ( ProductsList.getInstance().isLoaded() )
			{
				ProductsAdapter productsAdapter =
						new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( sTab.getTag().toString() ) );
				sProductsListView.setAdapter( productsAdapter );
				sProductsListView.setVisibility( ListView.VISIBLE );
//			    sProductsListView.setOnItemClickListener( products_adapter );
			}
		}
	}

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

				Intent productDetailsIntent = new Intent( ProductsListActivity.this, ProductDetailsActivity.class );

				productDetailsIntent.putExtra( "Product", selectedProduct );

				ProductsListActivity.this.startActivity( productDetailsIntent );
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
				Intent productDetailsIntent = new Intent( ProductsListActivity.this, ProductDetailsActivity.class );

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

	/**
	 * Method called when ProductsList has finished the products parsing process.
	 * It's necessary because it's possible to call to fillCategoryTab method without having finished the parsing process.
	 */
	public static void productsParseCompleted()
	{
		Log.d( TAG, "ProductsListActivity: productsParseCompleted" );

		ProductsAdapter productsAdapter =
				new ProductsAdapter( sProductsListActivity, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( sTab.getTag().toString() ) );
		sProductsListView.setAdapter( productsAdapter );
		sProductsListView.setVisibility( ListView.VISIBLE );
	}

	/******************************************************************************************************
	 * Categories & tabs
	 *****************************************************************************************************/

	/**
	 * Add to the sVisibleCategories array the current context sVisibleCategories
	 */
	private void initCategories()
	{
		sVisibleCategories.clear();

		// Get the xml/preferences.xml preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );

		for ( String categoryName : sAllCategories )
		{
			if ( preferences.getBoolean( categoryName.toLowerCase() + "_visible", true ) )
			{
				sVisibleCategories.add( categoryName );
			}
		}
	}

	/**
	 * Construct categories tabs
	 */
	private void initTabs()
	{
		sActionBar.removeAllTabs();

		for ( String categoryName : sVisibleCategories )
		{
			Tab tab = sActionBar.newTab();
			tab.setText( categoryName );
			tab.setTag( categoryName );
			tab.setTabListener( this );
			sActionBar.addTab( tab );
		}
	}

	/**
	 * Set navigation mode tabs in order to show the constructed tabs
	 * Also called by ProductsSearchView in onActionViewCollapsed event trigger
	 */
	public static void showTabs()
	{
		sActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

		sActionBar.setHomeButtonEnabled( false );
		sActionBar.setDisplayHomeAsUpEnabled( false );

		sActionBar.setTitle( sProductsListActivity.getResources().getString( R.string.app_name ) );
	}

	/**
	 * Hide tabs changing the navigation mode to standard
	 */
	public static void hideTabs()
	{
		sActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );

		sActionBar.setHomeButtonEnabled( true );
		sActionBar.setDisplayHomeAsUpEnabled( true );
	}

	/**
	 * Mandatory override
	 *
	 * @param tab         The sTab that was reselected.
	 * @param transaction
	 */
	@Override
	public void onTabReselected( Tab tab, FragmentTransaction transaction )
	{
		this.sTab = tab;
		this.sTransaction = transaction;
	}

	/**
	 * Tab change event listener
	 * fills the current category sTab with the required products
	 *
	 * @param tab         The sTab that was selected
	 * @param transaction
	 */
	@Override
	public void onTabSelected( Tab tab, FragmentTransaction transaction )
	{
		this.sTab = tab;
		this.sTransaction = transaction;

		if ( ProductsList.getInstance().isLoaded() )
		{
			ProductsAdapter productsAdapter =
					new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( tab.getTag().toString() ) );
			sProductsListView.setAdapter( productsAdapter );
			sProductsListView.setVisibility( ListView.VISIBLE );
		}
	}

	/**
	 * Mandatory override
	 *
	 * @param tab         The sTab that was unselected
	 * @param transaction
	 */
	@Override
	public void onTabUnselected( Tab tab, FragmentTransaction transaction )
	{
		this.sTab = tab;
		this.sTransaction = transaction;
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
		ProductsSearchView searchView = ( ProductsSearchView ) menu.findItem( R.id.mi_search ).getActionView();
		searchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );

		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		Log.d( TAG, "ProductsListActivity: onOptionsItemSelected: " + item.getTitle() );

		switch ( item.getItemId() )
		{
			case android.R.id.home:
				showTabs();
				return true;
			case R.id.mi_search:
				hideTabs();
				sProductsListView.setVisibility( ListView.INVISIBLE );
				return true;
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

	private void showBookmarksList()
	{
		hideTabs();

		if ( ProductsList.getInstance().isLoaded() )
		{
			ProductsAdapter productsAdapter =
					new ProductsAdapter( this, R.layout.products_list_entry,
							ProductsList.getInstance().getBookmarkedProducts() );
			sProductsListView.setAdapter( productsAdapter );
			sProductsListView.setVisibility( ListView.VISIBLE );
		}
	}
}
