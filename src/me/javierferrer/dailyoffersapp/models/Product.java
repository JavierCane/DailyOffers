package me.javierferrer.dailyoffersapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Product class
 */
public class Product implements Serializable
{

	private String mName;
	private String mDetailsUrl;
	private String mBuyUrl;
	private String mImage;
	private String mPrice;
	private String mOfferPrice;
	private String mProducer;
	private String mCategoryRoot;
	private String mCategoryLastChild;
	private ArrayList<HashMap<String, String>> mAttributes;

	public Product( String name, String detailsUrl, String buyUrl, String image, String price, String offerPrice, String producer, String categoryRoot,
	                String categoryLastChild, ArrayList<HashMap<String, String>> attributes )
	{
		this.mName = name;
		this.mDetailsUrl = detailsUrl;
		this.mBuyUrl = buyUrl;
		this.mImage = image;
		this.mPrice = price;
		this.mOfferPrice = offerPrice;
		this.mProducer = producer;
		this.mCategoryRoot = categoryRoot;
		this.mCategoryLastChild = categoryLastChild;
		this.mAttributes = attributes;
	}

	public String getName()
	{
		return mName;
	}

	public String getDetailsUrl()
	{
		return mDetailsUrl;
	}

	public String getBuyUrl()
	{
		return mBuyUrl;
	}

	public String getImage()
	{
		return mImage;
	}

	public String getPrice()
	{
		return mPrice;
	}

	public String getOfferPrice()
	{
		return mOfferPrice;
	}

	public String getProducer()
	{
		return mProducer;
	}

	public String getCategoryRoot()
	{
		return mCategoryRoot;
	}

	public String getCategoryLastChild()
	{
		return mCategoryLastChild;
	}

	public ArrayList<HashMap<String, String>> getAttributes()
	{
		return mAttributes;
	}

	@Override
	public String toString()
	{
		return "[mName: " + mName + ", mDetailsUrl:" + mDetailsUrl + ", mBuyUrl:" + mBuyUrl + ", mImage:" + mImage + ", mPrice:" + mPrice +
		       ", mOfferPrice:" + mOfferPrice + ", " + "mProducer:" + mProducer + ", mCategoryRoot:" + mCategoryRoot +
		       ", mCategoryLastChild:" + mCategoryLastChild + ", mAttributes:" + mAttributes.toString() + "]";
	}
}
