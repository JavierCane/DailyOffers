package me.javierferrer.dailyoffersapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductsAdapter extends ArrayAdapter<Product>
{

	ProductsListActivity products_list_activity;
	int textViewResourceId;
	Product current_product;
	ArrayList<Product> products;

	public ProductsAdapter( ProductsListActivity activity, int text_view_id, ArrayList<Product> products )
	{
		super( activity, text_view_id, products );

		this.textViewResourceId = text_view_id;
		this.products_list_activity = activity;
		this.products = products;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		View row = convertView;
		MyStringReaderHolder holder;

		if ( row == null )
		{
			LayoutInflater inflater = products_list_activity.getLayoutInflater();
			row = inflater.inflate( textViewResourceId, parent, false );

			holder = new MyStringReaderHolder();

			holder.name = ( TextView ) row.findViewById( R.id.tv_product_name );
			holder.image = ( ImageView ) row.findViewById( R.id.iv_product_image );
			holder.details = ( TextView ) row.findViewById( R.id.tv_product_details );

			row.setTag( holder );
		}
		else
		{
			holder = ( MyStringReaderHolder ) row.getTag();
		}

		Product product = products.get( position );

		holder.name.setText( product.getName() );
//		holder.image.setImageDrawable( context.getIdentifier( product.getImage() ) );
		holder.image.setImageResource( android.R.drawable.arrow_up_float );
		holder.details.setText( product.getPrice() );

		return row;
	}

	static class MyStringReaderHolder
	{
		TextView name, details;
		ImageView image;
	}
}
