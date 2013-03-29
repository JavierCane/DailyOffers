package me.javierferrer.dailyoffersapp.utils;

import me.javierferrer.dailyoffersapp.models.Product;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Products JSON Parser
 */
public class ProductsJSONParser
{
	private static final ProductsJSONParser sProductsJSONParserInstance = new ProductsJSONParser();

	private ProductsJSONParser()
	{
	}

	public static ProductsJSONParser getInstance()
	{
		return sProductsJSONParserInstance;
	}

	/**
	 * Parses a list of products based on a JSONArray
	 * Returns the products in a hashmap with the products category_root as the hashmap key and an ArrayList of Product as the HashMap values
	 *
	 * @param productsJsonArray
	 * @return a List of Products
	 */
	public static HashMap<String, ArrayList<Product>> parseAllProducts( JSONArray productsJsonArray )
	{
		int numProducts = productsJsonArray.length();
		HashMap<String, ArrayList<Product>> productsList = new HashMap<String, ArrayList<Product>>();
		Product product = null;

		/** Taking each country, parses and adds to list object */
		for ( int i = 0; i < numProducts; i++ )
		{
			try
			{
				// Parse the current product JSON
				product = parseProduct( ( JSONObject ) productsJsonArray.get( i ) );

				// If we've already parsed products from the same category, add it to the same ArrayList
				if ( productsList.get( product.getCategoryRoot() ) != null )
				{
					productsList.get( product.getCategoryRoot() ).add( product );
				}
				else // If we don't have any product in the same category key, create it
				{
					ArrayList<Product> categoryProducts = new ArrayList<Product>();
					categoryProducts.add( product );
					productsList.put( product.getCategoryRoot(), categoryProducts );
				}
			}
			catch ( JSONException e )
			{
				e.printStackTrace();
			}
		}

		return productsList;
	}

	/**
	 * Parses an individual product
	 *
	 * @param productJson
	 * @return product properties in a String-String HashMap based on a JSON product object
	 */
	private static Product parseProduct( JSONObject productJson )
	{
		Product product = null;

		Integer id;
		String name;
		String detailsUrl;
		String buyUrl;
		String image;
		String price;
		String offerPrice;
		String producer;
		String categoryRoot;
		String categoryLastChild;
		ArrayList<HashMap<String, String>> attributes = new ArrayList<HashMap<String, String>>();

		try
		{
			id = productJson.getInt( "id" );
			name = productJson.getString( "name" );
			detailsUrl = productJson.getString( "details_url" );
			buyUrl = productJson.getString( "buy_url" );
			image = productJson.getString( "image" );
			price = productJson.getString( "price" );
			offerPrice = productJson.getString( "offer_price" );
			producer = productJson.getString( "producer" );
			categoryRoot = productJson.getString( "category_root" );
			categoryLastChild = productJson.getString( "category_last_child" );

			// Parse attributes
			JSONArray attributesJson = productJson.getJSONArray( "attributes" );

			for ( int i = 0; i < attributesJson.length(); i++ )
			{
				try
				{
					// Parse the attributes array object
					attributes.add( getAttribute( ( JSONObject ) attributesJson.get( i ) ) );
				}
				catch ( JSONException e )
				{
					e.printStackTrace();
				}
			}

			product = new Product( id, name, detailsUrl, buyUrl, image, price, offerPrice, producer, categoryRoot, categoryLastChild, attributes );
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}

		return product;
	}

	/**
	 * Parse the product attribute JSON object
	 */
	private static HashMap<String, String> getAttribute( JSONObject attributesJson )
	{

		HashMap<String, String> attribute = new HashMap<String, String>();

		try
		{
			attribute.put( "key", attributesJson.getString( "key" ) );
			attribute.put( "value", attributesJson.getString( "value" ) );
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}

		return attribute;
	}
}
