package me.javierferrer.dailyoffersapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.actionbarsherlock.app.ActionBar.Tab;

public class ProductsListActivity extends SherlockListActivity implements ActionBar.TabListener
{

	private Tab tab;
	private FragmentTransaction transaction;

	SimpleCursorAdapter products_adapter;
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

		String json_string =
			"{ " +
                "\"products\":" +
				"[ " +
	                 "{" +
	                    "\"name\": \"Habana 7\"," +
	                    "\"image\": " + R.drawable.ic_refresh_inverse + "," +
	                    "\"price\": \"23.24\"," +
	                    "\"offer_price\": \"19.27\"," +
	                    "\"producer\": \"RhumJm\"," +
                        "\"categories\": {" +
		                    "\"root\": \"Spirits\", " +
	                        "\"last_child\": \"Rum\" " +
	                    "}," +
	                    "\"attributes\":" +
						"[ " +
							"{" +
		                        "\"key\": \"country\", " +
		                        "\"value\": \"Cuba\" " +
	                        "}," +
	                        "{" +
	                            "\"key\": \"volume\", " +
	                            "\"value\": \"70 cl\" " +
	                        "}" +
                        "]" +
	                 "}, " +
	                 "{" +
		                 "\"name\": \"Habana 5\"," +
		                 "\"image\": " + R.drawable.ic_refresh_inverse + "," +
		                 "\"price\": \"11.20\"," +
		                 "\"offer_price\": \"10.20\"," +
		                 "\"producer\": \"RhumJm\"," +
		                 "\"categories\": {" +
			                 "\"root\": \"Spirits\", " +
			                 "\"last_child\": \"Rum\" " +
		                 "}," +
		                 "\"attributes\":" +
		                 "[ " +
		                 "{" +
		                 "\"key\": \"country\", " +
		                 "\"value\": \"Cuba\" " +
		                 "}," +
		                 "{" +
		                 "\"key\": \"volume\", " +
		                 "\"value\": \"70 cl\" " +
		                 "}" +
		                 "]" +
	                 "}, " +
	                 "{" +
		                 "\"name\": \"Habana 3\"," +
		                 "\"image\": " + R.drawable.ic_refresh_inverse + "," +
		                 "\"price\": \"10.90\"," +
		                 "\"offer_price\": \"9.90\"," +
		                 "\"producer\": \"RhumJm\"," +
		                 "\"categories\": {" +
			                 "\"root\": \"Spirits\", " +
			                 "\"last_child\": \"Rum\" " +
		                 "}," +
		                 "\"attributes\":" +
		                 "[ " +
		                 "{" +
		                 "\"key\": \"country\", " +
		                 "\"value\": \"Cuba\" " +
		                 "}," +
		                 "{" +
		                 "\"key\": \"volume\", " +
		                 "\"value\": \"70 cl\" " +
		                 "}" +
		                 "]" +
	                 "}" +
                "]" +
            "}";

		/** The parsing of the xml data is done in a non-ui thread */
		ProductsLoader products_loader = new ProductsLoader( this );

		/** Start parsing xml data */
		products_loader.execute( json_string );
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
}
