package me.javierferrer.dailyoffersapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.activities.ProductsListBaseActivity;
import me.javierferrer.dailyoffersapp.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ProductsAdapter extends ArrayAdapter<Product>
{

	private int sTextViewId;

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Product> mProductsList;

	private final String mClassName = this.getClass().getSimpleName();

	private static class ProductViewHolder
	{

		ImageView image;
		TextView name, offerPrice, price;
		CheckBox favorited;
	}

	public ProductsAdapter( Context context, int textViewId, ArrayList<Product> productsList )
	{
		super( context, textViewId, productsList );

		sTextViewId = textViewId;

		mContext = context;
		mInflater = LayoutInflater.from( context );
		mProductsList = productsList;
	}

	@Override
	public int getCount()
	{
		return mProductsList.size();
	}

	@Override
	public Product getItem( int position )
	{
		return mProductsList.get( position );
	}

	@Override
	public long getItemId( int position )
	{
		return mProductsList.get( position ).getId();
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		View row = convertView;
		final ProductViewHolder holder;

		Product product = mProductsList.get( position );

		if ( row == null )
		{
			row = mInflater.inflate( sTextViewId, parent, false );

			holder = new ProductViewHolder();

			holder.image = ( ImageView ) row.findViewById( R.id.iv_product_image );
			holder.name = ( TextView ) row.findViewById( R.id.tv_product_name );
			holder.offerPrice = ( TextView ) row.findViewById( R.id.tv_product_offer_price );
			holder.price = ( TextView ) row.findViewById( R.id.tv_product_price );
			holder.favorited = ( CheckBox ) row.findViewById( R.id.cb_favorited_product );

			row.setTag( holder );
		}
		else
		{
			holder = ( ProductViewHolder ) row.getTag();
		}

		holder.image.setImageResource( ProductsListBaseActivity.getCategoriesImages().get( product.getCategoryRoot() ) );
		holder.name.setText( product.getName() );
		holder.offerPrice.setText( product.getOfferPrice() + "€" );
		holder.price.setText( mContext.getResources().getString( R.string.before ) + ": " + product.getPrice() + "€");
		holder.favorited.setTag( product );

		// Set listener for favorite action
		// Set as OnClickListener instead of OnCheckedChangeListener in order to do not trigger while setChecked() call
		holder.favorited.setOnClickListener( new CompoundButton.OnClickListener()
		{

			@Override
			public void onClick( View checkBoxView )
			{
				Product favoritedProduct = ( Product ) holder.favorited.getTag();

				ProductsListBaseActivity
						.setFavoriteProduct( favoritedProduct, ( ( CheckBox ) checkBoxView ).isChecked(),
								null );
			}
		} );

		holder.favorited.setChecked( product.isFavorited() );

		return row;
	}
}
