package me.javierferrer.dailyoffersapp.models;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.activities.ProductsByCategoryActivity;
import me.javierferrer.dailyoffersapp.activities.ProductsListBaseActivity;
import me.javierferrer.dailyoffersapp.utils.ProductsJSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
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
public final class ProductsList
{

	private static final ProductsList sProductsListInstance = new ProductsList();
	private Map<String, ArrayList<Product>> sProductsByCategory = new ConcurrentHashMap<String, ArrayList<Product>>();
	private final List<Product> sProductsList = new ArrayList<Product>();
	private List<Integer> sBookmarkedProductsIds = new ArrayList<Integer>();
	private boolean sLoaded = false;

	private final String BM_PRODUCTS_FILE_NAME = "bookmarked_products";

	/**
	 * Make a private constructor in order to do not allow public instantiation (Singleton class)
	 */
	private ProductsList()
	{
	}

	/**
	 * Returns the unique class instance (Singleton class)
	 *
	 * @return
	 */
	public static ProductsList getInstance()
	{
		return sProductsListInstance;
	}

	public boolean isLoaded()
	{
		return sLoaded;
	}

	/**
	 * Loads the products if they haven't been sLoaded already.
	 *
	 * @param resources Used to load the file containing the products.
	 * @param context
	 */
	public synchronized void ensureLoaded( final Resources resources, final Context context )
	{
		if ( !sLoaded )
		{
			new Thread( new Runnable()
			{

				public void run()
				{
					loadBookmarkedProducts( context );
					loadProducts( resources );
				}
			} ).start();
		}
	}

	private synchronized void loadProducts( Resources resources )
	{
		if ( !sLoaded )
		{
			Log.d( ProductsListBaseActivity.TAG, "ProductsList: Loading products" );

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
				sProductsByCategory = ProductsJSONParser.getInstance().parseAllProducts( productsJson.getJSONArray( "products" ), sBookmarkedProductsIds );

				// For each product category, add them to the complete products list
				for ( ArrayList<Product> categoryProducts : sProductsByCategory.values() )
				{
					sProductsList.addAll( categoryProducts );
				}

				Log.d( ProductsListBaseActivity.TAG, "ProductsList: Products loaded" );

				ProductsByCategoryActivity.productsParseCompleted();
			}
			catch ( JSONException e )
			{
				Log.e( ProductsListBaseActivity.TAG, "ProductsList: loadProducts: JSONException: " + e.toString() );
			}
			catch ( IOException e )
			{
				Log.e( ProductsListBaseActivity.TAG, "ProductsList: loadProducts: IOException trying to read JSON: " + e.toString() );
			}
			finally
			{
				try
				{
					jsonReader.close();
				}
				catch ( IOException e )
				{
					Log.e( ProductsListBaseActivity.TAG, "ProductsList: loadProducts: IOException trying to close JSON reader: " + e.toString() );
				}
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
	public ArrayList<Product> getFilteredProducts( String query, List<String> visibleCategories )
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
			Log.d( ProductsListBaseActivity.TAG, "products_list not sLoaded yet." );
		}

		return results;
	}

	public Map<String, ArrayList<Product>> getProductsByCategory()
	{
		return sProductsByCategory;
	}

	public ArrayList<Product> getCategoryProductsList( String category )
	{
		return sProductsByCategory.get( category );
	}

	private void loadBookmarkedProducts( Context context )
	{
		try
		{
			FileInputStream fis = context.openFileInput( BM_PRODUCTS_FILE_NAME );

			ObjectInputStream ois = new ObjectInputStream( fis );

			sBookmarkedProductsIds = ( List<Integer> ) ois.readObject();

			Log.d( ProductsListBaseActivity.TAG, "ProductsList: loadBookmarkedProducts: loaded: " + sBookmarkedProductsIds.toString() );

			ois.close();
		}
		catch ( FileNotFoundException e )
		{
			Log.d( ProductsListBaseActivity.TAG, "ProductsList: loadBookmarkedProducts: FileNotFoundException (Probably the user has not defined any bookmarked product yet)." );
		}
		catch ( StreamCorruptedException e )
		{
			Log.e( ProductsListBaseActivity.TAG, "ProductsList: loadBookmarkedProducts: StreamCorruptedException" );
		}
		catch ( OptionalDataException e )
		{
			Log.e( ProductsListBaseActivity.TAG, "ProductsList: loadBookmarkedProducts: OptionalDataException" );
		}
		catch ( IOException e )
		{
			Log.e( ProductsListBaseActivity.TAG, "ProductsList: loadBookmarkedProducts: IOException" );
		}
		catch ( ClassNotFoundException e )
		{
			Log.e( ProductsListBaseActivity.TAG, "ProductsList: loadBookmarkedProducts: ClassNotFoundException" );
		}
	}

	private void saveBookmarkedProducts( Context context )
	{
		try
		{
			OutputStream fos = context.openFileOutput( BM_PRODUCTS_FILE_NAME, Context.MODE_PRIVATE );

			fos.flush();
			ObjectOutputStream oos = new ObjectOutputStream( fos );
			oos.writeObject( sBookmarkedProductsIds );
			oos.flush();
			oos.close();

			Log.d( ProductsListBaseActivity.TAG, "ProductsList: loadBookmarkedProducts: saved: " + sBookmarkedProductsIds.toString() );
		}
		catch ( FileNotFoundException e )
		{
			Log.e( ProductsListBaseActivity.TAG, "ProductsList: saveBookmarkedProducts: FileNotFoundException" );
		}
		catch ( IOException e )
		{
			Log.e( ProductsListBaseActivity.TAG, "ProductsList: saveBookmarkedProducts: IOException" );
		}
	}

	/**
	 * Add or remove a product ID from the bookmarked products ID list.
	 *
	 * @param context   Application context (needed in order to call to openFileOutput() method while saving to internal storage)
	 * @param productId Product to add/remove ID
	 * @param add       Boolean indicating if we have to add (true) or remove (false) the product
	 */
	public void setBookmarkedProduct( Context context, Integer productId, Boolean add )
	{
		if ( add )
		{
			sBookmarkedProductsIds.add( productId );
		}
		else
		{
			sBookmarkedProductsIds.remove( productId );
		}

		saveBookmarkedProducts( context );
	}

	public ArrayList<Product> getBookmarkedProducts()
	{
		ArrayList<Product> bookmarkedProductsList = new ArrayList<Product>();

		for ( Integer bookmarkedProductId : sBookmarkedProductsIds )
		{
			for ( Product product : sProductsList )
			{
				if ( bookmarkedProductId.equals( product.getId() ) )
				{
					bookmarkedProductsList.add( product );
				}
			}
		}

		return bookmarkedProductsList;
	}
}
