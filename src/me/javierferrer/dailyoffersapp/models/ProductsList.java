package me.javierferrer.dailyoffersapp.models;

import android.content.res.Resources;
import android.util.Log;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.activities.ProductsListActivity;
import me.javierferrer.dailyoffersapp.utils.ProductsJSONParser;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: javierferrer
 * Date: 04/03/13
 * Time: 23:21
 * To change this template use File | Settings | File Templates.
 */
public class ProductsList
{

	private static final ProductsList instance = new ProductsList();

	public static ProductsList getInstance()
	{
		return instance;
	}

	private Map<String, ArrayList<Product>> products_by_category = new ConcurrentHashMap<String, ArrayList<Product>>();
	private final ArrayList<Product> products_list = new ArrayList<Product>();

	private ProductsList()
	{
	}

	private boolean loaded = false;

	/**
	 * Loads the words and definitions if they haven't been loaded already.
	 *
	 * @param resources Used to load the file containing the words and definitions.
	 */
	public synchronized void ensureLoaded( final Resources resources )
	{
		if ( !loaded )
		{
			new Thread( new Runnable()
			{

				public void run()
				{
					try
					{
						loadProducts( resources );
					}
					catch ( IOException e )
					{
						throw new RuntimeException( e );
					}
				}
			} ).start();
		}
	}

	private synchronized void loadProducts( Resources resources ) throws IOException
	{
		if ( !loaded )
		{
			Log.d( "DO", "ProductsList: Loading products" );

			ProductsJSONParser products_json_parser = new ProductsJSONParser();

			// Open a buffer in order to read from the products JSON file
			BufferedReader json_reader = new BufferedReader( new InputStreamReader( resources.openRawResource( R.raw.products ) ) );

			// Parse the products JSON file using the ProductsJSONParser class
			try
			{
				// Read all JSON file contents and put it in a StringBuilder
				StringBuilder json_builder = new StringBuilder();

				for ( String line = null; ( line = json_reader.readLine() ) != null; )
				{
					json_builder.append( line ).append( "\n" );
				}

				// Parse StringBuilder into a JSON Object
				JSONTokener json_tokener = new JSONTokener( json_builder.toString() );
				JSONObject products_json = new JSONObject( json_tokener );

				// Parse the JSON products object into the HashMap<String, ArrayList<Product>>
				products_by_category = products_json_parser.parseProducts( products_json.getJSONArray( "products" ) );

				// For each product category, add them to the complete products list
				for ( ArrayList<Product> category_products : products_by_category.values() )
				{
					products_list.addAll( category_products );
				}

				Log.d( "DO", "ProductsList: Products loaded" );
				ProductsListActivity.productsParseCompleted();
			}
			catch ( Exception e )
			{
				Log.e( "Exception trying to parse the json source: ", e.toString() );
			}
			finally
			{
				json_reader.close();
			}
		}

		loaded = true;
	}

	/**
	 * Get a list of products filtered by a string query on its name and a list of visible categories
	 *
	 * @param query
	 * @param visible_categories
	 * @return
	 */
	public ArrayList<Product> getFilteredProducts( String query, List<String> visible_categories )
	{
		ArrayList<Product> results = new ArrayList<Product>();

		if ( loaded )
		{
			if ( query.length() != 0 )
			{
				for ( Product product : products_list )
				{
					if ( visible_categories.contains( product.getCategoryRoot() ) && product.getName().toLowerCase().contains( query.toLowerCase() ) )
					{
						results.add( product );
					}
				}
			}
		}
		else
		{
			Log.d( "DO", "products_list not loaded yet." );
		}

		return results;
	}

	public Map<String, ArrayList<Product>> getProductsByCategory()
	{
		return products_by_category;
	}

	public ArrayList<Product> getCategoryProductsList( String category )
	{
		return products_by_category.get( category );
	}

	public boolean isLoaded()
	{
		return loaded;
	}
}
