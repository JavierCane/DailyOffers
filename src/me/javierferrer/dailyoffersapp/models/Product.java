package me.javierferrer.dailyoffersapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Product class
 */
public final class Product implements Serializable
{

	private Integer mId;
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
	private Boolean mFavorited;

	public Product( Integer id, String name, String detailsUrl, String buyUrl, String image, String price,
	                String offerPrice, String producer, String categoryRoot, String categoryLastChild,
	                ArrayList<HashMap<String, String>> attributes, Boolean favorited )
	{
		mId = id;
		mName = name;
		mDetailsUrl = detailsUrl;
		mBuyUrl = buyUrl;
		mImage = image;
		mPrice = price;
		mOfferPrice = offerPrice;
		mProducer = producer;
		mCategoryRoot = categoryRoot;
		mCategoryLastChild = categoryLastChild;
		mAttributes = attributes;
		mFavorited = favorited;
	}

	public Integer getId()
	{
		return mId;
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

	public Boolean isFavorited()
	{
		return mFavorited;
	}

	public void setFavorited( Boolean favorited )
	{
		mFavorited = favorited;
	}

	@Override
	public String toString()
	{
		return "[mName: " + mName + ", mDetailsUrl:" + mDetailsUrl + ", mBuyUrl:" + mBuyUrl + ", mImage:" + mImage +
		       ", mPrice:" + mPrice + ", mOfferPrice:" + mOfferPrice + ", mProducer:" + mProducer +
		       ", mCategoryRoot:" + mCategoryRoot + ", mCategoryLastChild:" + mCategoryLastChild +
		       ", mAttributes:" + mAttributes.toString() + ", mFavorited:" + mFavorited.toString() + "]";
	}

	@Override
	public boolean equals( Object o )
	{
		if ( this == o )
		{
			return true;
		}
		if ( !( o instanceof Product ) )
		{
			return false;
		}

		Product product = ( Product ) o;

		if ( !mId.equals( product.mId ) )
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return mId.hashCode();
	}
}
