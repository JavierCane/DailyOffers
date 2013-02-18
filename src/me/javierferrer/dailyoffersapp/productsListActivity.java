package me.javierferrer.dailyoffersapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
		// Application theme
		setTheme( R.style.Theme_Sherlock_Light );

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

		// Parse JSON file
		products_loader.execute();
	}

	private void setListListeners()
	{
		products_list_view.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				Product selected_product = ( Product ) products_list_view.getItemAtPosition( position );
				Toast.makeText( getApplicationContext(), "Click, item: " + selected_product.getName(), Toast.LENGTH_SHORT ).show();
			}
		} );
	}

	/**
	 * Add to the categories array the current context categories
	 */
	public void constructCategories()
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
//		if ( v.getId() == products_list_view.getId() )
//		{
		AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) menu_info;

		Product selected_product = ( Product ) products_list_view.getItemAtPosition( info.position );

		menu.setHeaderTitle( selected_product.getName() );
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.products_list_menu, menu );
//		}
	}

	@Override
	public boolean onContextItemSelected( android.view.MenuItem item )
	{
		AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) item.getMenuInfo();
//		IcsAdapterView.AdapterContextMenuInfo info = ( IcsAdapterView.AdapterContextMenuInfo ) item.getMenuInfo();

		Product selected_product = ( Product ) products_list_view.getItemAtPosition( info.position );

		switch ( item.getItemId() )
		{
			case R.id.mi_view_details:
				Toast.makeText( getApplicationContext(), "View item: " + selected_product.getName(), Toast.LENGTH_SHORT ).show();
				return true;
			case R.id.mi_buy:
				Toast.makeText( getApplicationContext(), "Buy item: " + selected_product.getName(), Toast.LENGTH_SHORT ).show();
				return true;
			default:
				return super.onContextItemSelected( item );
		}
	}
}
