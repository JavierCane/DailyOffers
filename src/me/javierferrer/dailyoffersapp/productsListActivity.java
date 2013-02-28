package me.javierferrer.dailyoffersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import java.util.ArrayList;

import static com.actionbarsherlock.app.ActionBar.Tab;

public class ProductsListActivity extends SherlockActivity implements ActionBar.TabListener
{

	private static Tab tab;
	private static FragmentTransaction transaction;
	private static ListView products_list_view;

	private final ArrayList<String> categories = new ArrayList<String>();

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		// Action Bar Sherlock compatibility
		super.onCreate( savedInstanceState );

		// Layout
		setContentView( R.layout.products_list );

		// Get categories
		constructCategories();

		// Construct tabs
		constructTabs();

		// Set products list listeners
		products_list_view = ( ListView ) findViewById( R.id.products_list );
		registerForContextMenu( products_list_view ); // Set the list view long clickable and responding with a context menu
		setListListeners();

		// Parse JSON products in a non-ui thread
		ProductsLoader products_loader = new ProductsLoader( this, products_list_view );
		products_loader.execute(); // Call to doInBackground mehtod
	}

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
	 * Add to the categories array the current context categories
	 */
	private void constructCategories()
	{
		categories.add( "Wines" );
		categories.add( "Spirits" );
		categories.add( "Beers" );
	}

	/**
	 * Construct categories tabs
	 */
	private void constructTabs()
	{
		getSupportActionBar().setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

		for ( String category_name : categories )
		{
			Tab tab = getSupportActionBar().newTab();
			tab.setText( category_name );
			tab.setTag( category_name + "_tag" );
			tab.setTabListener( this );
			getSupportActionBar().addTab( tab );
		}
	}

	@Override
	public void onTabReselected( Tab tab, FragmentTransaction transaction )
	{
		this.tab = tab;
		this.transaction = transaction;
	}

	@Override
	public void onTabSelected( Tab tab, FragmentTransaction transaction )
	{
		this.tab = tab;
		this.transaction = transaction;
		Log.d( "DO", "onTabSelected: tab getTag=" + tab.getTag());
		Log.d( "DO", "onTabSelected: tab getContentDescription=" + tab.getContentDescription());
		Log.d( "DO", "onTabSelected: tab getPosition=" + tab.getPosition());
		Log.d( "DO", "onTabSelected: tab getText=" + tab.getText());
/*
02-24 20:21:33.484: DEBUG/DO(24516): onTabSelected: tab getTag=null
02-24 20:21:33.484: DEBUG/DO(24516): onTabSelected: tab getContentDescription=null
02-24 20:21:33.484: DEBUG/DO(24516): onTabSelected: tab getPosition=1
02-24 20:21:33.484: DEBUG/DO(24516): onTabSelected: tab getText=Spirits
 */
//		switch (mTabHost.getCurrentTab()) {
//			case 0:
//				myDevAdapter.getFilter().filter("all");
//				break;
//			case 1:
//				myDevAdapter.getFilter().filter("type1");
//				break;
//			case 2:
//				myDevAdapter.getFilter().filter("type2");
//				break;
//
//			default:
//				Log.d(debugTag,"error in onTabChanged");
//				break;
//		}
	}

	@Override
	public void onTabUnselected( Tab tab, FragmentTransaction transaction )
	{
		this.tab = tab;
		this.transaction = transaction;
	}

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
				bundle.putString( "url", selected_product.getBuy_url() );
				intent.putExtras( bundle );

				startActivity( intent );

				return true;
			default:
				return super.onContextItemSelected( item );
		}
	}
}
