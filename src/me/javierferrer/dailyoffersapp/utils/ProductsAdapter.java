package me.javierferrer.dailyoffersapp.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import me.javierferrer.dailyoffersapp.activities.ProductsListActivity;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.Product;

import java.util.ArrayList;

public class ProductsAdapter extends ArrayAdapter<Product>
{

	private static ProductsListActivity sProductsListActivity;
	private static ArrayList<Product> sProductsList;
	private static int sTextViewId;

	private static class ProductViewHolder
	{
		TextView name, details;
		ImageView image;
	}

	public ProductsAdapter( ProductsListActivity productsListActivity, int textViewId, ArrayList<Product> productsList )
	{
		super( productsListActivity, textViewId, productsList );

		this.sProductsListActivity = productsListActivity;
		this.sTextViewId = textViewId;
		this.sProductsList = productsList;
	}


	@Override
	public View getView( int position, View view, ViewGroup parentViewGroup )
	{
		View row = view;
		ProductViewHolder holder;

		if ( row == null )
		{
			LayoutInflater inflater = sProductsListActivity.getLayoutInflater();
			row = inflater.inflate( sTextViewId, parentViewGroup, false );

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

		Product product = sProductsList.get( position );

		holder.name.setText( product.getName() );
//		holder.image.setImageResource( sProductsListActivity.getResources().getIdentifier( product.getImage(), "drawable", sProductsListActivity.getPackageName() ) );
		holder.details.setText( product.getPrice() );

		return row;
	}

}
