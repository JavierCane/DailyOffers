package me.javierferrer.dailyoffersapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;

import static com.actionbarsherlock.app.ActionBar.Tab;

public class ProductsListActivity extends SherlockActivity implements ActionBar.TabListener
{

	private static Tab tab;
	private static FragmentTransaction transaction;
	private static ListView products_list_view;
	private static ActionBar action_bar;

	private final ArrayList<String> categories = new ArrayList<String>();

	private static final String TAG = "DO";

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		// Action Bar Sherlock compatibility
		super.onCreate( savedInstanceState );

		// Layout
		setContentView( R.layout.products_list );

		// Get categories
		initCategories();

		// Construct tabs
		initTabs();

		// Set products list listeners
		products_list_view = ( ListView ) findViewById( R.id.products_list );
		registerForContextMenu( products_list_view ); // Set the list view long clickable and responding with a context menu
		setListListeners();

		// Parse JSON products in a non-ui thread
		ProductsLoader products_loader = new ProductsLoader( this, products_list_view );
		products_loader.execute(); // Call to doInBackground mehtod

		action_bar.setHomeButtonEnabled( true );
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
	 * Method called when ProductsLoader has finished the products parsing process.
	 * It's necessary because it's possible to call to fillCategoryTab method without having finished the parsing process.
	 */
	public static void productsParseCompleted()
	{
		ProductsLoader.fillCategoryTab( tab.getTag().toString() );
	}

	/******************************************************************************************************
	 * Tabs
	 *****************************************************************************************************/

	/**
	 * Add to the categories array the current context categories
	 */
	private void initCategories()
	{
		categories.add( "Wines" );
		categories.add( "Spirits" );
		categories.add( "Beers" );
	}

	/**
	 * Construct categories tabs
	 */
	private void initTabs()
	{
		this.action_bar = getSupportActionBar();

		for ( String category_name : categories )
		{
			Tab tab = action_bar.newTab();
			tab.setText( category_name );
			tab.setTag( category_name );
			tab.setTabListener( this );
			action_bar.addTab( tab );
		}

		// Set tabs navigation mode
		showTabs();
	}

	/**
	 * Set navigation mode tabs in order to show the constructed tabs
	 * Also called by ProductsSearchView in onActionViewCollapsed event trigger
	 */
	public static void showTabs()
	{
		action_bar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
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

		ProductsLoader.fillCategoryTab( tab.getTag().toString() );
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

		ProductsSearchView search_view = ( ProductsSearchView ) menu.findItem( R.id.search_btn ).getActionView();
		search_view.setQueryHint( getResources().getString( R.string.search_hint ) ); // Set search query hint

		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case R.id.search_btn:
				// Hide tabs changing the navigation mode to standard
				action_bar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );

				// Get the search edit text and add the on change listener
				EditText search = ( EditText ) item.getActionView().findViewById( R.id.abs__search_src_text );
				search.addTextChangedListener( filter_products_text_watcher );
				search.requestFocus();
				InputMethodManager imm = ( InputMethodManager ) getSystemService( Context.INPUT_METHOD_SERVICE );
				imm.toggleSoftInput( InputMethodManager.SHOW_FORCED, 0 );
				break;
			default:
				return super.onOptionsItemSelected( item );
		}

		return true;
	}

	private TextWatcher filter_products_text_watcher = new TextWatcher()
	{

		public void afterTextChanged( Editable search )
		{
		}

		public void beforeTextChanged( CharSequence search, int start, int count, int after )
		{
		}

		public void onTextChanged( CharSequence search, int start, int before, int count )
		{
			ProductsLoader.fillProductsList( ProductsLoader.getFilteredProducts( search ) );
		}
	};
}
