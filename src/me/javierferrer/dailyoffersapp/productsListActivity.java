package me.javierferrer.dailyoffersapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

import java.util.ArrayList;

import static com.actionbarsherlock.app.ActionBar.Tab;

public class ProductsListActivity extends SherlockActivity implements ActionBar.TabListener
{

	private static Tab tab;
	private static FragmentTransaction transaction;
	private static ListView products_list_view;

	public ProductsAdapter products_adapter;
	public ArrayList<Product> products_list;
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
		setListListeners();

		// Parse JSON products in a non-ui thread
		ProductsLoader products_loader = new ProductsLoader( this, products_list_view, products_adapter );

		// Parse JSON file
		products_loader.execute();
	}

	private void setListListeners()
	{
		registerForContextMenu( products_list_view ); // Set the list view long clickable and responding with a context menu

		products_list_view.setOnCreateContextMenuListener( new View.OnCreateContextMenuListener()
		{

			@Override
			public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo )
			{
				if ( v.getId() == android.R.id.list )

				{
					AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) menuInfo;

					Product selected_product = ( Product ) products_list_view.getItemAtPosition( info.position );
					Toast.makeText( getApplicationContext(), "Click, item: " + selected_product.toString(), Toast.LENGTH_LONG ).show();

					menu.setHeaderTitle( "Hi!" );
					menu.add( Menu.NONE, 0, 0, "View details" );
					menu.add( Menu.NONE, 0, 1, "Buy" );
				}
			}
		} );

		products_list_view.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				Object o = products_list_view.getItemAtPosition( position );
				String pen = o.toString();
				Toast.makeText( getApplicationContext(), "Click, item: " + " " + pen, Toast.LENGTH_LONG ).show();
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
}
