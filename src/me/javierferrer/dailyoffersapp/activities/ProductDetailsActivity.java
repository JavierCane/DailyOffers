package me.javierferrer.dailyoffersapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.Product;

import java.util.HashMap;

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

	private RelativeLayout mLayout;
	private ImageView mIvProductImage;
	private TextView mTvProductOfferPrice;
	private TextView mTvProductPrice;
	private TextView mTvProductCategory;
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

		initViewVariables();

		fillViewVariables();

		constructAttributesViews();
	}

	private void initActionBar()
	{
		mActionBar = getSupportActionBar();

		mActionBar.setHomeButtonEnabled( true );
		mActionBar.setDisplayHomeAsUpEnabled( true );
		mActionBar.setTitle( mProduct.getName() );
	}

	private void initViewVariables()
	{
		mLayout = ( RelativeLayout ) findViewById( R.id.rl_product_details );

		mIvProductImage = ( ImageView ) findViewById( R.id.iv_product_image );
		mTvProductOfferPrice = ( TextView ) findViewById( R.id.tv_product_offer_price );
		mTvProductPrice = ( TextView ) findViewById( R.id.tv_product_price );
		mCbFavorited = ( CheckBox ) findViewById( R.id.cb_favorited_product );
		mTvProductCategory = ( TextView ) findViewById( R.id.tv_product_category );
	}

	private void fillViewVariables()
	{
		mIvProductImage
				.setImageResource( ProductsListBaseActivity.getCategoriesImages().get( mProduct.getCategoryRoot() ) );
		mTvProductOfferPrice
				.setText( getResources().getString( R.string.offer_price ) + ": " + mProduct.getOfferPrice() + "€" );
		mTvProductPrice
				.setText( getResources().getString( R.string.regular_price ) + ": " + mProduct.getPrice() + "€" );
		mTvProductCategory.setText(
				getResources().getString( R.string.category ) + ": " + mProduct.getCategoryRoot() + " > " +
				mProduct.getCategoryLastChild() );

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

		mBtnBuyProduct = ( Button ) findViewById( R.id.btn_buy );

		// Buy button onClick callback -> Init webview activity
		mBtnBuyProduct.setOnClickListener( new View.OnClickListener()
		{

			public void onClick( View view )
			{
				Intent intent = new Intent( ProductDetailsActivity.this, WebViewActivity.class );

				Bundle bundle = new Bundle();
				bundle.putString( "url", mProduct.getBuyUrl() );
				intent.putExtras( bundle );

				startActivity( intent );
			}
		} );
	}

	private void constructAttributesViews()
	{
		int attributeIterator = 555;

		for ( HashMap<String, String> attribute : mProduct.getAttributes() )
		{
			TextView tv = new TextView( getApplicationContext() );
			tv.setId( attributeIterator );
			tv.setText( attribute.get( "key" ) + ": " + attribute.get( "value" ) );
			tv.setTextSize( 15 );
			tv.setTextColor( Color.BLACK );

			RelativeLayout.LayoutParams params =
					new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT );
			params.addRule( RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE );
			params.setMargins( 30, 5, 0, 0 ); // Left, top, right, bottom.

			if ( attributeIterator == 555 )
			{
				params.addRule( RelativeLayout.BELOW, R.id.tv_product_category );
			}
			else
			{
				params.addRule( RelativeLayout.BELOW, attributeIterator - 1 );
			}

			mLayout.addView( tv, params );
			attributeIterator++;
		}
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
