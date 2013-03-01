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
	private static HashMap<String, ArrayList<Product>> complete_products_list = new HashMap<String, ArrayList<Product>>();

	public ProductsLoader( ProductsListActivity products_list_activity, ListView products_list_view )
	{
		this.products_list_activity = products_list_activity;
		this.products_list_view = products_list_view;
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
	protected void onPostExecute( HashMap<String, ArrayList<Product>> complete_products_list )
	{
		super.onPostExecute( complete_products_list );

		// Set the complete product array list
		this.complete_products_list = complete_products_list;

		// Call to ProductsListActivity method in order to notify that we'va finished the products parsing process
		ProductsListActivity.productsParseCompleted();
	}

	/**
	 * Fills each products_list_entry layout with one product from complete_products_list throug the ProductsAdapter class
	 *
	 * @param tab_tag
	 */
	public static void fillCategoryTab( String tab_tag )
	{
		// If we already have parsed all products and we've them on the complete_products_list variable, fill up the layout entries trough the adapter
		// Since we call to this method from ProductsListActivity.onTabSelected method which is called while initializing the app,
		// is possible to arrive here without having all products parsed
		if ( !complete_products_list.isEmpty() )
		{
			// Get the products from the selected category
			ArrayList<Product> category_products = complete_products_list.get( tab_tag );

			// If there'is any product inside the selected category
			if ( category_products != null )
			{
				ProductsAdapter products_adapter = new ProductsAdapter( ProductsLoader.products_list_activity, R.layout.products_list_entry, category_products );

				products_list_view.setAdapter( products_adapter );

				products_list_view.setVisibility( ListView.VISIBLE );
			}
		}
	}
}
