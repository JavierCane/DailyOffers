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

	private static final String TAG = "DO";

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		Log.d( TAG, "ProductsListActivity: onCreate" );

		// Action Bar Sherlock compatibility
		super.onCreate( savedInstanceState );

		// Firstly we need to ensure that the products list has been loaded and if not, try to load it!
		ProductsList.getInstance().ensureLoaded( getResources() );

		// Layout
		setContentView( R.layout.products_list );

		// Configure the Action Bar
		sActionBar = getSupportActionBar();
		sProductsListView = ( ListView ) findViewById( R.id.products_list );
		sLayoutInflater = this.getLayoutInflater();
		sProductsListActivity = this;

		Log.d( TAG, "ProductsListActivity: onCreate: Ready to handle the intent. Action bar: " + sActionBar.toString() + ", sProductsListView: " + sProductsListView.toString() );

		// Get sVisibleCategories
		initCategories();

		// Construct tabs
		initTabs();

		// Handle possible search intent
		handleIntent( getIntent() );

		Log.d( TAG, "ProductsListActivity: onCreate: Intent hadled" );

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
			ProductsAdapter products_adapter;
			if ( preferences.getBoolean( "visibility_affects_search", true ) )
			{
				products_adapter = new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getFilteredProducts( query, sVisibleCategories ) );
			}
			else
			{
				products_adapter = new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getFilteredProducts( query, sAllCategories ) );
			}

			sProductsListView.setAdapter( products_adapter );
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
				ProductsAdapter products_adapter =
						new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( sTab.getTag().toString() ) );
				sProductsListView.setAdapter( products_adapter );
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
				Product selected_product = ( Product ) sProductsListView.getItemAtPosition( position );

				Intent product_details_intent = new Intent( ProductsListActivity.this, ProductDetailsActivity.class );

				product_details_intent.putExtra( "Product", selected_product );

				ProductsListActivity.this.startActivity( product_details_intent );
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
//		if ( v.getId() == sProductsListView.getId() )
//		{
		AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) menuInfo;

		Product selected_product = ( Product ) sProductsListView.getItemAtPosition( info.position );

		menu.setHeaderTitle( selected_product.getName() );
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.products_list_menu, menu );
//		}
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

		Product selected_product = ( Product ) sProductsListView.getItemAtPosition( info.position );

		switch ( item.getItemId() )
		{
			// In case of view product details menu item click, create an intent to the product details activity
			case R.id.mi_view_details:
				Intent product_details_intent = new Intent( ProductsListActivity.this, ProductDetailsActivity.class );

				product_details_intent.putExtra( "Product", selected_product );

				startActivity( product_details_intent );

				return true;
			// In case of buy product menu item click, create an intent to redirect to the product buy site
			case R.id.mi_buy:
				Intent intent = new Intent( this.getBaseContext(), WebViewActivity.class );

				Bundle bundle = new Bundle();
				bundle.putString( "url", selected_product.getBuyUrl() );
				intent.putExtras( bundle );

				startActivity( intent );

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

		ProductsAdapter products_adapter =
				new ProductsAdapter( sProductsListActivity, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( sTab.getTag().toString() ) );
		sProductsListView.setAdapter( products_adapter );
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

		for ( String category_name : sAllCategories )
		{
			if ( preferences.getBoolean( category_name.toLowerCase() + "_visible", true ) )
			{
				sVisibleCategories.add( category_name );
			}
		}
	}

	/**
	 * Construct categories tabs
	 */
	private void initTabs()
	{
		sActionBar.removeAllTabs();

		for ( String category_name : sVisibleCategories )
		{
			Tab tab = sActionBar.newTab();
			tab.setText( category_name );
			tab.setTag( category_name );
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
			ProductsAdapter products_adapter =
					new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( tab.getTag().toString() ) );
			sProductsListView.setAdapter( products_adapter );
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
	 * Action Bar (search and settings)
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
		SearchManager search_manager = ( SearchManager ) getSystemService( Context.SEARCH_SERVICE );
		ProductsSearchView search_view = ( ProductsSearchView ) menu.findItem( R.id.mi_search ).getActionView();
		search_view.setSearchableInfo( search_manager.getSearchableInfo( getComponentName() ) );

		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		Log.d( TAG, "ProductsListActivity: onOptionsItemSelected: " + item.getTitle() );

		switch ( item.getItemId() )
		{
			case R.id.mi_search:
				hideTabs();
				sProductsListView.setVisibility( ListView.INVISIBLE );
				return true;
			case android.R.id.home:
				showTabs();
				return true;
			case R.id.mi_settings:
				Intent settings_activity = new Intent( getBaseContext(), PreferencesActivity.class );
				startActivity( settings_activity );
			default:
				return super.onOptionsItemSelected( item );
		}
	}
}
