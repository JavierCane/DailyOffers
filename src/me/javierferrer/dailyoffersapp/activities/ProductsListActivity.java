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

	private static Tab tab;
	private static FragmentTransaction transaction;
	private static ListView products_list_view;
	private static ActionBar action_bar;
	private static LayoutInflater layout_inflater;
	private static ProductsListActivity products_list_activity;

	private static final List<String> all_categories = new ArrayList<String>( asList( "Wines", "Spirits", "Beers" ) );
	private static final List<String> visible_categories = new ArrayList<String>();

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
		action_bar = getSupportActionBar();
		products_list_view = ( ListView ) findViewById( R.id.products_list );
		layout_inflater = this.getLayoutInflater();
		products_list_activity = this;

		Log.d( TAG, "ProductsListActivity: onCreate: Ready to handle the intent. Action bar: " + action_bar.toString() + ", products_list_view: " + products_list_view.toString() );

		// Get visible_categories
		initCategories();

		// Construct tabs
		initTabs();

		// Handle possible search intent
		handleIntent( getIntent() );

		Log.d( TAG, "ProductsListActivity: onCreate: Intent hadled" );

		// Set products list listeners
		registerForContextMenu( products_list_view ); // Set the list view long clickable and responding with a context menu
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

		// Get visible_categories
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

			action_bar.setTitle( getResources().getString( R.string.search ) + ": \"" + query + "\"" );

			Log.d( TAG, "ProductsListActivity: handleIntent: search intent, query: " + query );

			// Get the xml/preferences.xml preferences
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );

			// Check if the user has specified that the categories filters affects on search results
			ProductsAdapter products_adapter;
			if ( preferences.getBoolean( "visibility_affects_search", true ) )
			{
				products_adapter = new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getFilteredProducts( query, visible_categories ) );
			}
			else
			{
				products_adapter = new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getFilteredProducts( query, all_categories ) );
			}

			products_list_view.setAdapter( products_adapter );
			products_list_view.setVisibility( ListView.VISIBLE );
//			mTextView.setText(getString(R.string.search_results, query));
//			mList.setOnItemClickListener(wordAdapter);
		}
		else // if ( Intent.ACTION_MAIN.equals( intent.getAction() ) )
		{
			Log.d( TAG, "ProductsListActivity: handleIntent: main action intent detected" );

			// Set tabs navigation mode
			showTabs();

			Log.d( TAG, "ProductsListActivity: handleIntent: products_list_view: " + products_list_view.toString() );

			if ( ProductsList.getInstance().isLoaded() )
			{
				ProductsAdapter products_adapter =
						new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( tab.getTag().toString() ) );
				products_list_view.setAdapter( products_adapter );
				products_list_view.setVisibility( ListView.VISIBLE );
//			    products_list_view.setOnItemClickListener( products_adapter );
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
		products_list_view.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				Product selected_product = ( Product ) products_list_view.getItemAtPosition( position );

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
	 * @param v
	 * @param menu_info
	 */
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menu_info )
	{
		// TODO: Revisar si en otras pantallas sale el menú o no
//		if ( v.getId() == products_list_view.getId() )
//		{
		AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) menu_info;

		Product selected_product = ( Product ) products_list_view.getItemAtPosition( info.position );

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

		Product selected_product = ( Product ) products_list_view.getItemAtPosition( info.position );

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
				new ProductsAdapter( products_list_activity, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( tab.getTag().toString() ) );
		products_list_view.setAdapter( products_adapter );
		products_list_view.setVisibility( ListView.VISIBLE );
	}

	/******************************************************************************************************
	 * Categories & tabs
	 *****************************************************************************************************/

	/**
	 * Add to the visible_categories array the current context visible_categories
	 */
	private void initCategories()
	{
		visible_categories.clear();

		// Get the xml/preferences.xml preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );

		for ( String category_name : all_categories )
		{
			if ( preferences.getBoolean( category_name.toLowerCase() + "_visible", true ) )
			{
				visible_categories.add( category_name );
			}
		}
	}

	/**
	 * Construct categories tabs
	 */
	private void initTabs()
	{
		action_bar.removeAllTabs();

		for ( String category_name : visible_categories )
		{
			Tab tab = action_bar.newTab();
			tab.setText( category_name );
			tab.setTag( category_name );
			tab.setTabListener( this );
			action_bar.addTab( tab );
		}
	}

	/**
	 * Set navigation mode tabs in order to show the constructed tabs
	 * Also called by ProductsSearchView in onActionViewCollapsed event trigger
	 */
	public static void showTabs()
	{
		action_bar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

		action_bar.setHomeButtonEnabled( false );
		action_bar.setDisplayHomeAsUpEnabled( false );

		action_bar.setTitle( products_list_activity.getResources().getString( R.string.app_name ) );
	}

	/**
	 * Hide tabs changing the navigation mode to standard
	 */
	public static void hideTabs()
	{
		action_bar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );

		action_bar.setHomeButtonEnabled( true );
		action_bar.setDisplayHomeAsUpEnabled( true );
	}

	/**
	 * Mandatory override
	 *
	 * @param tab         The tab that was reselected.
	 * @param transaction
	 */
	@Override
	public void onTabReselected( Tab tab, FragmentTransaction transaction )
	{
		this.tab = tab;
		this.transaction = transaction;
	}

	/**
	 * Tab change event listener
	 * fills the current category tab with the required products
	 *
	 * @param tab         The tab that was selected
	 * @param transaction
	 */
	@Override
	public void onTabSelected( Tab tab, FragmentTransaction transaction )
	{
		this.tab = tab;
		this.transaction = transaction;

		if ( ProductsList.getInstance().isLoaded() )
		{
			ProductsAdapter products_adapter =
					new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( tab.getTag().toString() ) );
			products_list_view.setAdapter( products_adapter );
			products_list_view.setVisibility( ListView.VISIBLE );
		}
	}

	/**
	 * Mandatory override
	 *
	 * @param tab         The tab that was unselected
	 * @param transaction
	 */
	@Override
	public void onTabUnselected( Tab tab, FragmentTransaction transaction )
	{
		this.tab = tab;
		this.transaction = transaction;
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
		ProductsSearchView search_view = ( ProductsSearchView ) menu.findItem( R.id.search_btn ).getActionView();
		search_view.setSearchableInfo( search_manager.getSearchableInfo( getComponentName() ) );

		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		Log.d( TAG, "ProductsListActivity: onOptionsItemSelected: " + item.getTitle() );

		switch ( item.getItemId() )
		{
			case R.id.search_btn:
				hideTabs();
				products_list_view.setVisibility( ListView.INVISIBLE );
				return true;
			case android.R.id.home:
				showTabs();
				return true;
			case R.id.settings_btn:
				Intent settings_activity = new Intent( getBaseContext(), PreferencesActivity.class );
				startActivity( settings_activity );
			default:
				return super.onOptionsItemSelected( item );
		}
	}
}
