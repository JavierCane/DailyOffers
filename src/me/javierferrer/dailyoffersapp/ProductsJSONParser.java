package me.javierferrer.dailyoffersapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Products JSON Parser
 */
public class ProductsJSONParser
{

	/**
	 * Parses a list of products based on a JSONArray
	 *
	 * @param products_json_array
	 * @return a List of String-String HashMaps with one list item per product
	 */
	public List<HashMap<String, String>> parseProducts( JSONArray products_json_array )
	{
		int num_products = products_json_array.length();
		List<HashMap<String, String>> products_list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> product = null;

		/** Taking each country, parses and adds to list object */
		for ( int i = 0; i < num_products; i++ )
		{
			try
			{
				/** Call getProduct with country JSON object to parse the country */
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
	private HashMap<String, String> getProduct( JSONObject product_json )
	{

		HashMap<String, String> product = new HashMap<String, String>();
		String name;
		String image;
		String price;
		String offer_price;
		String producer;
		String category_root;
		String category_last_child;
		String attributes = "";
		HashMap<String, String> attribute;
		JSONArray attributes_json;

		try
		{
			name = product_json.getString( "name" );
			image = product_json.getString( "image" );
			price = product_json.getString( "price" );
			offer_price = product_json.getString( "offer_price" );
			producer = product_json.getString( "producer" );
			category_root = product_json.getJSONObject( "categories" ).getString( "root" );
			category_last_child = product_json.getJSONObject( "categories" ).getString( "last_child" );

			attributes_json = product_json.getJSONArray( "attributes" );

			int num_attributes = attributes_json.length();

			for ( int i = 0; i < num_attributes; i++ )
			{
				try
				{
					// Parse the attributes array object
					attribute = getAttribute( ( JSONObject ) attributes_json.get( i ) );

					attributes += attribute.get( "key" ) + ": " + attribute.get( "value" );
					if ( i < num_attributes - 1 )
					{
						attributes += "\n";
					}
				}
				catch ( JSONException e )
				{
					e.printStackTrace();
				}
			}

			String details = "Price: " + price + "\n" +
			                 "Offer price: " + offer_price + "\n" +
			                 "Producer: " + producer + "\n" +
			                 "Category: " + category_last_child + " (" + category_root + ")";

			if ( !attributes.isEmpty() )
			{
				details += "\n" + attributes;
			}

			product.put( "name", name );
			product.put( "image", image );
			product.put( "details", details );
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
