package me.javierferrer.dailyoffersapp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Product class
 */
public class Product
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
		this.producer = category_root;
		this.producer = category_last_child;
		this.attributes = attributes;
	}

	public String getName()
	{
		return name;
	}

	public String getDetails_url()
	{
		return details_url;
	}

	public String getBuy_url()
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

	public String getOffer_price()
	{
		return offer_price;
	}

	public String getProducer()
	{
		return producer;
	}

	public String getCategory_root()
	{
		return category_root;
	}

	public String getCategory_last_child()
	{
		return category_last_child;
	}

	public ArrayList<HashMap<String, String>> getAttributes()
	{
		return attributes;
	}
}
