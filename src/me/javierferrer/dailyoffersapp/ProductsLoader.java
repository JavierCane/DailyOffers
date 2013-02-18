package me.javierferrer.dailyoffersapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * Products loader
 */
public class ProductsLoader extends AsyncTask<String, Void, SimpleAdapter>
{

	ProductsListActivity products_list_activity;

	public ProductsLoader( ProductsListActivity products_list_activity )
	{
		this.products_list_activity = products_list_activity;
	}

	/**
	 * Load and parse the products JSON file in a non-ui thread
	 *
	 * @param params
	 * @return
	 */
	@Override
	protected SimpleAdapter doInBackground( String... params )
	{
		ProductsJSONParser products_json_parser = new ProductsJSONParser();

		List<HashMap<String, String>> products = null;

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
			products = products_json_parser.parseProducts( products_json.getJSONArray( "products" ) );
		}
		catch ( Exception e )
		{
			Log.d( "Exception trying to parse the json source: ", e.toString() );
		}

		// Keys used in products list HashMaps
		String[] from = {
				"name",
				"image",
				"details"
		};

		// IDs of views in products_list_entry
		int[] to = {
				R.id.tv_product_name,
				R.id.iv_product_image,
				R.id.tv_product_details
		};

		// Initialize an adapter to store each item
		SimpleAdapter adapter = new SimpleAdapter( products_list_activity.getBaseContext(), products, R.layout.products_list_entry, from, to );

		return adapter;
	}

	/**
	 * This will be executed in ui thread.
	 * Invoked by the Android system on "doInBackground"
	 *
	 * @param adapter
	 */
	@Override
	protected void onPostExecute( SimpleAdapter adapter )
	{

		/** Getting a reference to listview of main.xml layout file */
		ListView listView = ( ListView ) products_list_activity.findViewById( android.R.id.list );

		/** Setting the adapter containing the country list to listview */
		listView.setAdapter( adapter );
	}
}
