package me.javierferrer.dailyoffersapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;

import java.util.ArrayList;
import java.util.List;

import static com.actionbarsherlock.app.ActionBar.Tab;

public class ProductsListActivity extends SherlockListActivity implements ActionBar.TabListener
{

	private Tab tab;
	private FragmentTransaction transaction;
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

		// [Start] Create tabs
		categories.add( "Wines" );
		categories.add( "Spirits" );
		categories.add( "Beers" );

		getSupportActionBar().setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

		for ( String category_name : categories )
		{
			Tab tab = getSupportActionBar().newTab();
			tab.setText( category_name );
			tab.setTabListener( this );
			getSupportActionBar().addTab( tab );
		}
		// [End] Create tabs

		// [Start] Get tab content
		// Create an array of Strings, that will be put to our ListActivity
		ArrayAdapter<Product> adapter = new ArrayAdapter( this, R.layout.products_list_entry, getProducts() );
		setListAdapter( adapter );
		// [End] Get tab content
	}

	private List<Product> getProducts()
	{
		List<Product> list = new ArrayList<Product>();

		list.add( get( "Habana 7", "Rum", 23.24, 19.27, "image/path" ) );
		list.add( get( "Habana 6", "Rum", 23.24, 19.27, "image/path" ) );
		list.add( get( "Habana 5", "Rum", 23.24, 19.27, "image/path" ) );
		list.add( get( "Habana 4", "Rum", 23.24, 19.27, "image/path" ) );
		list.add( get( "Habana 3", "Rum", 23.24, 19.27, "image/path" ) );
		list.add( get( "Habana 2", "Rum", 23.24, 19.27, "image/path" ) );
		list.add( get( "Habana 1", "Rum", 23.24, 19.27, "image/path" ) );

		return list;
	}

	private Product get( String name, String category, Double price, Double offer_price, String image_path )
	{
		return new Product( name, category, price, offer_price, image_path );
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
