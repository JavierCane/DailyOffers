package me.javierferrer.dailyoffersapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Products loader
 */
public class ProductsLoader extends AsyncTask<String, Void, HashMap<String, ArrayList<Product>>>
{

	private static ProductsListActivity products_list_activity;
	private static ListView products_list_view;
	private static HashMap<String, ArrayList<Product>> products_by_category = new HashMap<String, ArrayList<Product>>();

	private static ArrayList<Product> products_list = new ArrayList<Product>();

	public ProductsLoader( ProductsListActivity products_list_activity, ListView products_list_view )
	{
		this.products_list_activity = products_list_activity;
		this.products_list_view = products_list_view;
	}

	/**
	 * products_list getter
	 *
	 * @return products_list
	 */
	public ArrayList<Product> getProductsList()
	{
		return products_list;
	}

	/**
	 * Load and parse the products JSON file in a non-ui thread
	 *
	 * @param params
	 * @return
	 */
	@Override
	protected HashMap<String, ArrayList<Product>> doInBackground( String... params )
	{
		HashMap<String, ArrayList<Product>> parsed_products_list = new HashMap<String, ArrayList<Product>>();

		ProductsJSONParser products_json_parser = new ProductsJSONParser();

		// Parse the products JSON file using the ProductsJSONParser class
		try
		{
			// Open a buffer in order to read from the products JSON file
			BufferedReader json_reader = new BufferedReader( new InputStreamReader( products_list_activity.getResources().openRawResource( R.raw.products ) ) );

			// Read all JSON file contents and put it in a StringBuilder
			StringBuilder json_builder = new StringBuilder();

			for ( String line = null; ( line = json_reader.readLine() ) != null; )
			{
				json_builder.append( line ).append( "\n" );
			}

			// Parse StringBuilder into a JSON Object
			JSONTokener json_tokener = new JSONTokener( json_builder.toString() );
			JSONObject products_json = new JSONObject( json_tokener );

			// Parse the JSON products object into the List<HashMap<String, String>>
			parsed_products_list = products_json_parser.parseProducts( products_json.getJSONArray( "products" ) );
		}
		catch ( Exception e )
		{
			Log.e( "Exception trying to parse the json source: ", e.toString() );
		}

		// Return the parsed products list in order to revieve it in onPostExecute method as input
		return parsed_products_list;
	}

	/**
	 * This will be executed in ui thread.
	 * Invoked by the Android system on "doInBackground"
	 */
	@Override
	protected void onPostExecute( HashMap<String, ArrayList<Product>> products_by_category )
	{
		super.onPostExecute( products_by_category );

		// Set the structured by category product array list
		this.products_by_category = products_by_category;

		// Set the complete products list
		for ( ArrayList<Product> category_products : products_by_category.values() )
		{
			this.products_list.addAll( category_products );
		}

		// Call to ProductsListActivity method in order to notify that we'va finished the products parsing process
		ProductsListActivity.productsParseCompleted();
	}

	/**
	 * Fills each products_list_entry layout with one product from products_by_category throug the ProductsAdapter class
	 *
	 * @param tab_tag
	 */
	public static void fillCategoryTab( String tab_tag )
	{
		// If we already have parsed all products and we've them on the products_by_category variable, fill up the layout entries trough the adapter
		// Since we call to this method from ProductsListActivity.onTabSelected method which is called while initializing the app,
		// is possible to arrive here without having all products parsed
		if ( !products_by_category.isEmpty() )
		{
			// Get the products from the selected category
			ArrayList<Product> category_products = products_by_category.get( tab_tag );

			// If there'is any product inside the selected category
			if ( category_products != null )
			{
				ProductsAdapter products_adapter = new ProductsAdapter( products_list_activity, R.layout.products_list_entry, category_products );

				products_list_view.setAdapter( products_adapter );

				products_list_view.setVisibility( ListView.VISIBLE );
			}
		}
	}

	public static ArrayList<Product> getFilteredProducts( CharSequence search )
	{
		ArrayList<Product> results = new ArrayList<Product>();

		if ( search.length() != 0 )
		{
			for ( Product product : products_list )
			{
				if ( product.getName().toLowerCase().contains( search.toString().toLowerCase() ) )
				{
					results.add( product );
				}
			}
		}

		return results;
	}

	/**
	 * Fills each products_list_entry layout with one product from products_by_category throug the ProductsAdapter class
	 */
	public static void fillProductsList( ArrayList<Product> products_list )
	{
		ProductsAdapter products_adapter = new ProductsAdapter( products_list_activity, R.layout.products_list_entry, products_list );

		products_list_view.setAdapter( products_adapter );

		products_list_view.setVisibility( ListView.VISIBLE );
	}
}
