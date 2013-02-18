package me.javierferrer.dailyoffersapp;

import android.util.Log;
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

	/**
	 * Parses a list of products based on a JSONArray
	 *
	 * @param products_json_array
	 * @return a List of Products
	 */
	public ArrayList<Product> parseProducts( JSONArray products_json_array )
	{
		int num_products = products_json_array.length();
		ArrayList<Product> products_list = new ArrayList<Product>();
		Product product = null;

		/** Taking each country, parses and adds to list object */
		for ( int i = 0; i < num_products; i++ )
		{
			try
			{
				// Parse the current product JSON
				product = getProduct( ( JSONObject ) products_json_array.get( i ) );

				products_list.add( product );
			}
			catch ( JSONException e )
			{
				e.printStackTrace();
			}
		}

		return products_list;
	}

	/**
	 * Parses an individual product
	 *
	 * @param product_json
	 * @return product properties in a String-String HashMap based on a JSON product object
	 */
	private Product getProduct( JSONObject product_json )
	{
		Product product = null;

		String name;
		String details_url;
		String buy_url;
		String image;
		String price;
		String offer_price;
		String producer;
		String category_root;
		String category_last_child;
		ArrayList<HashMap<String, String>> attributes = new ArrayList<HashMap<String, String>>();

		try
		{
			name = product_json.getString( "name" );
			details_url = product_json.getString( "details_url" );
			buy_url = product_json.getString( "buy_url" );
			image = product_json.getString( "image" );
			price = product_json.getString( "price" );
			offer_price = product_json.getString( "offer_price" );
			producer = product_json.getString( "producer" );
			category_root = product_json.getJSONObject( "categories" ).getString( "root" );
			category_last_child = product_json.getJSONObject( "categories" ).getString( "last_child" );

			// Parse attributes
			JSONArray attributes_json = product_json.getJSONArray( "attributes" );

			for ( int i = 0; i < attributes_json.length(); i++ )
			{
				try
				{
					// Parse the attributes array object
					attributes.add( getAttribute( ( JSONObject ) attributes_json.get( i ) ) );
				}
				catch ( JSONException e )
				{
					e.printStackTrace();
				}
			}

			product = new Product( name, details_url, buy_url, image, price, offer_price, producer, category_root, category_last_child, attributes );
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
	private HashMap<String, String> getAttribute( JSONObject attribute_json )
	{

		HashMap<String, String> attribute = new HashMap<String, String>();

		try
		{
			attribute.put( "key", attribute_json.getString( "key" ) );
			attribute.put( "value", attribute_json.getString( "value" ) );
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}

		return attribute;
	}
}
