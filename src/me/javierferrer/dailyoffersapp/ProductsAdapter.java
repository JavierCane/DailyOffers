package me.javierferrer.dailyoffersapp;

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
	ArrayList<Product> products_list;
	int text_view_id;

	public ProductsAdapter( ProductsListActivity products_list_activity, int text_view_id, ArrayList<Product> products_list )
	{
		super( products_list_activity, text_view_id, products_list );

		this.products_list_activity = products_list_activity;
		this.text_view_id = text_view_id;
		this.products_list = products_list;
	}

	@Override
	public View getView( int position, View view, ViewGroup parent_view_group )
	{
		View row = view;
		ProductViewHolder holder;

		if ( row == null )
		{
			LayoutInflater inflater = products_list_activity.getLayoutInflater();
			row = inflater.inflate( text_view_id, parent_view_group, false );

			holder = new ProductViewHolder();

			holder.name = ( TextView ) row.findViewById( R.id.tv_product_name );
			holder.image = ( ImageView ) row.findViewById( R.id.iv_product_image );
			holder.details = ( TextView ) row.findViewById( R.id.tv_product_details );

			row.setTag( holder );
		}
		else
		{
			holder = ( ProductViewHolder ) row.getTag();
		}

		Product product = products_list.get( position );

		holder.name.setText( product.getName() );
//		holder.image.setImageResource( products_list_activity.getResources().getIdentifier( product.getImage(), "drawable", products_list_activity.getPackageName() ) );
		holder.details.setText( product.getPrice() );

		return row;
	}

	static class ProductViewHolder
	{

		TextView name, details;
		ImageView image;
	}
}
