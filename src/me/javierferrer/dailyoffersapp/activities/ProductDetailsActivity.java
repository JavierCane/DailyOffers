package me.javierferrer.dailyoffersapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.Product;

/**
 * Created with IntelliJ IDEA.
 * User: javierferrer
 * Date: 24/02/13
 * Time: 19:13
 * To change this template use File | Settings | File Templates.
 */
public final class ProductDetailsActivity extends SherlockActivity
{

	private Product mProduct;
	private ActionBar mActionBar;

	private ImageView mIvProductImage;
	private TextView mTvProductOfferPrice;
	private TextView mTvProductPrice;
	private CheckBox mCbFavorited;
	private Button mBtnBuyProduct;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.product_details );

		Intent intent = getIntent();
		mProduct = ( Product ) intent.getSerializableExtra( "Product" );

		initActionBar();

		mIvProductImage = ( ImageView ) findViewById( R.id.iv_product_image );
		mTvProductOfferPrice = ( TextView ) findViewById( R.id.tv_product_offer_price );
		mTvProductPrice = ( TextView ) findViewById( R.id.tv_product_price );
		mCbFavorited = ( CheckBox ) findViewById( R.id.cb_favorited_product );

		mIvProductImage
				.setImageResource( ProductsListBaseActivity.getCategoriesImages().get( mProduct.getCategoryRoot() ) );
		mTvProductOfferPrice
				.setText( getResources().getString( R.string.offer_price ) + ": " + mProduct.getOfferPrice() + "€" );
		mTvProductPrice.setText( getResources().getString( R.string.regular_price ) + ": " + mProduct.getPrice() + "€" );

		mCbFavorited.setTag( mProduct );

		// Set listener for favorite action
		// Set as OnClickListener instead of OnCheckedChangeListener in order to do not trigger while setChecked() call
		mCbFavorited.setOnClickListener( new CompoundButton.OnClickListener()
		{

			@Override
			public void onClick( View checkBoxView )
			{
				Product favoritedProduct = ( Product ) mCbFavorited.getTag();

				ProductsListBaseActivity
						.setFavoriteProduct( favoritedProduct, ( ( CheckBox ) checkBoxView ).isChecked(), null );
			}
		} );

		mCbFavorited.setChecked( mProduct.isFavorited() );

		final Button btnBuyProduct = ( Button ) findViewById( R.id.btn_buy );
		btnBuyProduct.setOnClickListener( new View.OnClickListener()
		{

			public void onClick( View view )
			{
				// Perform action on click
			}
		} );
	}

	private void initActionBar()
	{
		mActionBar = getSupportActionBar();

		mActionBar.setHomeButtonEnabled( true );
		mActionBar.setDisplayHomeAsUpEnabled( true );
		mActionBar.setTitle( mProduct.getName() );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case android.R.id.home:
				Intent intent = new Intent( this, ProductsByCategoryActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );
				return true;
			default:
				return super.onOptionsItemSelected( item );
		}
	}
}
