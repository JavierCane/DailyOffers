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

	private static final ProductsList sProductsListInstance = new ProductsList();
	private static Map<String, ArrayList<Product>> sProductsByCategory = new ConcurrentHashMap<String, ArrayList<Product>>();
	private static final ArrayList<Product> sProductsList = new ArrayList<Product>();
	private static boolean sLoaded = false;

	private ProductsList()
	{
	}

	public static ProductsList getInstance()
	{
		return sProductsListInstance;
	}

	/**
	 * Loads the products if they haven't been sLoaded already.
	 *
	 * @param resources Used to load the file containing the products.
	 */
	public static synchronized void ensureLoaded( final Resources resources )
	{
		if ( !sLoaded )
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

	private static synchronized void loadProducts( Resources resources ) throws IOException
	{
		if ( !sLoaded )
		{
			Log.d( "DO", "ProductsList: Loading products" );

			// Open a buffer in order to read from the products JSON file
			BufferedReader jsonReader = new BufferedReader( new InputStreamReader( resources.openRawResource( R.raw.products ) ) );

			// Parse the products JSON file using the ProductsJSONParser class
			try
			{
				// Read all JSON file contents and put it in a StringBuilder
				StringBuilder jsonBuilder = new StringBuilder();

				for ( String line = null; ( line = jsonReader.readLine() ) != null; )
				{
					jsonBuilder.append( line ).append( "\n" );
				}

				// Parse StringBuilder into a JSON Object
				JSONTokener jsonTokener = new JSONTokener( jsonBuilder.toString() );
				JSONObject productsJson = new JSONObject( jsonTokener );

				// Parse the JSON products object into the HashMap<String, ArrayList<Product>>
				sProductsByCategory = ProductsJSONParser.getInstance().parseAllProducts( productsJson.getJSONArray( "products" ) );

				// For each product category, add them to the complete products list
				for ( ArrayList<Product> categoryProducts : sProductsByCategory.values() )
				{
					sProductsList.addAll( categoryProducts );
				}

				Log.d( "DO", "ProductsList: Products sLoaded" );
				ProductsListActivity.productsParseCompleted();
			}
			catch ( Exception e )
			{
				Log.e( "Exception trying to parse the json source: ", e.toString() );
			}
			finally
			{
				jsonReader.close();
			}
		}

		sLoaded = true;
	}

	/**
	 * Get a list of products filtered by a string query on its name and a list of visible categories
	 *
	 * @param query
	 * @param visibleCategories
	 * @return
	 */
	public static ArrayList<Product> getFilteredProducts( String query, List<String> visibleCategories )
	{
		ArrayList<Product> results = new ArrayList<Product>();

		if ( sLoaded )
		{
			if ( query.length() != 0 )
			{
				for ( Product product : sProductsList )
				{
					if ( visibleCategories.contains( product.getCategoryRoot() ) && product.getName().toLowerCase().contains( query.toLowerCase() ) )
					{
						results.add( product );
					}
				}
			}
		}
		else
		{
			Log.d( "DO", "products_list not sLoaded yet." );
		}

		return results;
	}

	public static Map<String, ArrayList<Product>> getProductsByCategory()
	{
		return sProductsByCategory;
	}

	public static ArrayList<Product> getCategoryProductsList( String category )
	{
		return sProductsByCategory.get( category );
	}

	public static boolean isLoaded()
	{
		return sLoaded;
	}
}
