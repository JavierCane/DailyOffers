package me.javierferrer.dailyoffersapp.models;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import me.javierferrer.dailyoffersapp.activities.ProductsByCategoryActivity;
import me.javierferrer.dailyoffersapp.activities.ProductsListBaseActivity;
import me.javierferrer.dailyoffersapp.utils.ProductsJSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: javierferrer
 * Date: 04/03/13
 * Time: 23:21
 * To change this template use File | Settings | File Templates.
 */
public final class ProductsList extends AsyncTask<InputStream, Void, Void>
{

	public static final String FAVORITES_FILE_NAME = "favorited_products_2";
	private static final ProductsList sProductsListInstance = new ProductsList();

	private final List<Product> sProductsList = new ArrayList<Product>();
	private Map<String, ArrayList<Product>> sProductsByCategory;
	private static Set<Integer> sFavoritedProductsIds =  new HashSet();
	private static boolean sLoaded = false;

	private static final String sClassName = "ProductsList";

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

	@Override
	protected Void doInBackground( InputStream... productsJsonFile )
	{
		Log.d( ProductsListBaseActivity.TAG, sClassName + "\t" + "doInBackground: sLoaded: " + sLoaded );

		if ( !sLoaded )
		{
			loadProducts( productsJsonFile );
		}

		return null;
	}

	@Override
	protected void onPostExecute( Void dummyVariable )
	{
		Log.d( ProductsListBaseActivity.TAG, sClassName + "\t" + "onPostExecute" );
		sLoaded = true;
		ProductsByCategoryActivity.productsParseCallback();
	}

	private void loadProducts( InputStream[] productsJsonFile )
	{
		Log.d( ProductsListBaseActivity.TAG, sClassName + "\t\t\t\t" + "loadProducts: Loading products" );

		// Open a buffer in order to read from the products JSON file
		BufferedReader jsonReader = new BufferedReader( new InputStreamReader( productsJsonFile[0] ) );

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
			sProductsByCategory = ProductsJSONParser.getInstance()
					.parseAllProducts( productsJson.getJSONArray( "products" ), sFavoritedProductsIds );

			// For each product category, add them to the complete products list
			for ( ArrayList<Product> categoryProducts : sProductsByCategory.values() )
			{
				sProductsList.addAll( categoryProducts );
			}

			Log.d( ProductsListBaseActivity.TAG, sClassName + "\t\t\t\t" + "loadProducts: Products loaded" );
		}
		catch ( JSONException e )
		{
			Log.e( ProductsListBaseActivity.TAG,
					sClassName + "\t\t\t\t" + "loadProducts: JSONException: " + e.toString() );
		}
		catch ( IOException e )
		{
			Log.e( ProductsListBaseActivity.TAG,
					sClassName + "\t\t\t\t" + "loadProducts: IOException trying to read JSON: " + e.toString() );
		}
		finally
		{
			try
			{
				jsonReader.close();
			}
			catch ( IOException e )
			{
				Log.e( ProductsListBaseActivity.TAG,
						sClassName + "\t\t\t\t" + "loadProducts: IOException trying to close JSON reader: " +
						e.toString() );
			}
		}
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
					if ( visibleCategories.contains( product.getCategoryRoot() ) &&
					     product.getName().toLowerCase().contains( query.toLowerCase() ) )
					{
						results.add( product );
					}
				}
			}
		}
		else
		{
			Log.d( ProductsListBaseActivity.TAG, sClassName + "\t\t\t\t" + "sProductsList not sLoaded yet." );
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

	public static void loadFavoritedProducts( FileInputStream favoritesFileInputStream )
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream( favoritesFileInputStream );

			sFavoritedProductsIds = ( Set<Integer> ) ois.readObject();

			Log.d( ProductsListBaseActivity.TAG,
					sClassName + "\t\t\t\t" + "loadFavoritedProducts: loaded: " + sFavoritedProductsIds.toString() );

			ois.close();
		}
		catch ( StreamCorruptedException e )
		{
			Log.e( ProductsListBaseActivity.TAG,
					sClassName + "\t\t\t\t" + "loadFavoritedProducts: StreamCorruptedException" );
		}
		catch ( OptionalDataException e )
		{
			Log.e( ProductsListBaseActivity.TAG,
					sClassName + "\t\t\t\t" + "loadFavoritedProducts: OptionalDataException" );
		}
		catch ( IOException e )
		{
			Log.e( ProductsListBaseActivity.TAG, sClassName + "\t\t\t\t" + "loadFavoritedProducts: IOException" );
		}
		catch ( ClassNotFoundException e )
		{
			Log.e( ProductsListBaseActivity.TAG,
					sClassName + "\t\t\t\t" + "loadFavoritedProducts: ClassNotFoundException" );
		}
	}

	private void saveFavoritedProducts( Context context )
	{
		try
		{
			OutputStream fos = context.openFileOutput( FAVORITES_FILE_NAME, Context.MODE_PRIVATE );

			fos.flush();
			ObjectOutputStream oos = new ObjectOutputStream( fos );
			oos.writeObject( sFavoritedProductsIds );
			oos.flush();
			oos.close();

			Log.d( ProductsListBaseActivity.TAG,
					sClassName + "\t\t\t\t" + "loadFavoritedProducts: saved: " + sFavoritedProductsIds.toString() );
		}
		catch ( FileNotFoundException e )
		{
			Log.e( ProductsListBaseActivity.TAG,
					sClassName + "\t\t\t\t" + "saveFavoritedProducts: FileNotFoundException" );
		}
		catch ( IOException e )
		{
			Log.e( ProductsListBaseActivity.TAG, sClassName + "\t\t\t\t" + "saveFavoritedProducts: IOException" );
		}
	}

	/**
	 * Add or remove a product ID from the favorited products ID list.
	 *
	 * @param context   Application context (needed in order to call to openFileOutput() method while saving to internal storage)
	 * @param productId Product to add/remove ID
	 * @param add       Boolean indicating if we have to add (true) or remove (false) the product
	 */
	public void setFavoritedProduct( Context context, Integer productId, Boolean add )
	{
		if ( add )
		{
			sFavoritedProductsIds.add( productId );
		}
		else
		{
			sFavoritedProductsIds.remove( productId );
		}

		saveFavoritedProducts( context );
	}

	public ArrayList<Product> getFavoritedProducts()
	{
		ArrayList<Product> favoritedProductsList = new ArrayList<Product>();

		for ( Integer favoritedProductId : sFavoritedProductsIds )
		{
			for ( Product product : sProductsList )
			{
				if ( favoritedProductId.equals( product.getId() ) )
				{
					favoritedProductsList.add( product );
				}
			}
		}

		return favoritedProductsList;
	}
}
