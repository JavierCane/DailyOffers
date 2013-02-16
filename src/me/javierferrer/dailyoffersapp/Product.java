package me.javierferrer.dailyoffersapp;

/**
 * Created with IntelliJ IDEA.
 * User: javierferrer
 * Date: 16/02/13
 * Time: 23:39
 * To change this template use File | Settings | File Templates.
 */
public class Product
{

	private String name;
	private String category;
	private Double price;
	private Double offer_price;
	private String image_path;

	public Product( String name, String category, Double price, Double offer_price, String image_path )
	{
		this.name = name;
		this.category = category;
		this.price = price;
		this.offer_price = offer_price;
		this.image_path = image_path;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory( String category )
	{
		this.category = category;
	}

	public Double getPrice()
	{
		return price;
	}

	public void setPrice( Double price )
	{
		this.price = price;
	}

	public Double getOffer_price()
	{
		return offer_price;
	}

	public void setOffer_price( Double offer_price )
	{
		this.offer_price = offer_price;
	}

	public String getImage_path()
	{
		return image_path;
	}

	public void setImage_path( String image_path )
	{
		this.image_path = image_path;
	}
}
