package me.javierferrer.dailyoffersapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Products loader
 */
public class ProductsLoader extends AsyncTask<String, Void, Void>
{

	ProductsListActivity products_list_activity;
	ListView products_list_view;
	ProductsAdapter products_adapter;

	public ProductsLoader( ProductsListActivity products_list_activity, ListView products_list_view, ProductsAdapter products_adapter )
	{
		this.products_list_activity = products_list_activity;
		this.products_list_view = products_list_view;
		this.products_adapter = products_adapter;
	}

	/**
	 * Load and parse the products JSON file in a non-ui thread
	 *
	 * @param params
	 * @return
	 */
	@Override
	protected Void doInBackground( String... params )
	{
		ProductsJSONParser products_json_parser = new ProductsJSONParser();

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
			products_list_activity.products_list = products_json_parser.parseProducts( products_json.getJSONArray( "products" ) );
		}
		catch ( Exception e )
		{
			Log.e( "Exception trying to parse the json source: ", e.toString() );
		}

		return null;
	}

	/**
	 * This will be executed in ui thread.
	 * Invoked by the Android system on "doInBackground"
	 */
	@Override
	protected void onPostExecute( Void params )
	{
		super.onPostExecute( params );

		products_adapter = new ProductsAdapter( products_list_activity, R.layout.products_list_entry, products_list_activity.products_list );

		products_list_view.setAdapter( products_adapter );

		products_list_activity.products_adapter = products_adapter;

		products_list_view.setVisibility( ListView.VISIBLE );
	}
}
