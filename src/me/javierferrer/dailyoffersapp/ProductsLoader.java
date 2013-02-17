package me.javierferrer.dailyoffersapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Products loader
 */
public class ProductsLoader extends AsyncTask<String, Void, SimpleAdapter>
{

	JSONObject json_object;
	ProductsListActivity products_list_activity;

	public ProductsLoader( ProductsListActivity products_list_activity )
	{
		this.products_list_activity = products_list_activity;
	}

	/**
	 * Doing the parsing of xml data in a non-ui thread
	 */
	@Override
	protected SimpleAdapter doInBackground( String... json_source )
	{
		ProductsJSONParser products_json_parser = new ProductsJSONParser();

		List<HashMap<String, String>> products = null;

		try
		{
			json_object = new JSONObject( json_source[0] );
			/** Getting the parsed data as a List construct */
			products = products_json_parser.parse( json_object );

		}
		catch ( Exception e )
		{
			Log.d( "Exception trying to parse the json source: ", e.toString() );
		}

		/** Keys used in Hashmap */
		String[] from = {
				"name",
				"image",
				"details"
		};

		/** Ids of views in listview_layout */
		int[] to = {
				R.id.tv_product_name,
				R.id.iv_product_image,
				R.id.tv_product_details
		};

		/** Instantiating an adapter to store each items
		 *  R.layout.listview_layout defines the layout of each item
		 */
		SimpleAdapter adapter = new SimpleAdapter( products_list_activity.getBaseContext(), products, R.layout.products_list_entry, from, to );

		return adapter;
	}

	/** Invoked by the Android system on "doInBackground" is executed completely */
	/**
	 * This will be executed in ui thread
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
