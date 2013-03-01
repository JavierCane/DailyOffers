package me.javierferrer.dailyoffersapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Product class
 */
public class Product implements Serializable
{

	private String name;
	private String details_url;
	private String buy_url;
	private String image;
	private String price;
	private String offer_price;
	private String producer;
	private String category_root;
	private String category_last_child;
	private ArrayList<HashMap<String, String>> attributes;

	public Product( String name, String details_url, String buy_url, String image, String price, String offer_price, String producer, String category_root,
	                String category_last_child, ArrayList<HashMap<String, String>> attributes )
	{
		this.name = name;
		this.details_url = details_url;
		this.buy_url = buy_url;
		this.image = image;
		this.price = price;
		this.offer_price = offer_price;
		this.producer = producer;
		this.category_root = category_root;
		this.category_last_child = category_last_child;
		this.attributes = attributes;
	}

	public String getName()
	{
		return name;
	}

	public String getDetailsUrl()
	{
		return details_url;
	}

	public String getBuyUrl()
	{
		return buy_url;
	}

	public String getImage()
	{
		return image;
	}

	public String getPrice()
	{
		return price;
	}

	public String getOfferPrice()
	{
		return offer_price;
	}

	public String getProducer()
	{
		return producer;
	}

	public String getCategoryRoot()
	{
		return category_root;
	}

	public String getCategoryLastChild()
	{
		return category_last_child;
	}

	public ArrayList<HashMap<String, String>> getAttributes()
	{
		return attributes;
	}

	@Override
	public String toString()
	{
		return "[name: " + name + ", details_url:" + details_url + ", buy_url:" + buy_url + ", image:" + image + ", price:" + price +
		       ", offer_price:" + offer_price + ", " + "producer:" + producer + ", category_root:" + category_root +
		       ", category_last_child:" + category_last_child + ", attributes:" + attributes.toString() + "]";
	}
}
