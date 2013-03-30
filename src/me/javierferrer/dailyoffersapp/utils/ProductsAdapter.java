package me.javierferrer.dailyoffersapp.utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.activities.ProductsListBaseActivity;
import me.javierferrer.dailyoffersapp.models.Product;

import java.util.ArrayList;

public final class ProductsAdapter extends ArrayAdapter<Product>
{

	private static ProductsListBaseActivity sProductsListBaseActivity;
	private static ArrayList<Product> sProductsList;
	private static int sTextViewId;

	private static class ProductViewHolder
	{

		TextView name, details;
		ImageView image;
	}

	public ProductsAdapter( ProductsListBaseActivity productsListBaseActivity, int textViewId,
	                        ArrayList<Product> productsList )
	{
		super( productsListBaseActivity, textViewId, productsList );

		this.sProductsListBaseActivity = productsListBaseActivity;
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
			LayoutInflater inflater = sProductsListBaseActivity.getLayoutInflater();
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

		if ( position < sProductsList.size() )
		{
			Product product = sProductsList.get( position );

			holder.name.setText( product.getName() );
//		    holder.image.setImageResource( sProductsListBaseActivity.getResources().getIdentifier( product.getImage(), "drawable", sProductsListBaseActivity.getPackageName() ) );
			holder.details.setText( product.getPrice() );
		}
		else
		{
			Log.e( ProductsListBaseActivity.TAG,
					"ProductsAdapter: getView: Trying to get an index that is greater than the current parsed products count: " +
					position + "/" + sProductsList.size() );
		}

		return row;
	}
}
