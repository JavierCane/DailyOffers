package me.javierferrer.dailyoffersapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
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

	private static ActionBar sActionBar;
	private Product mProduct;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.product_details );

		sActionBar = getSupportActionBar();
		Intent intent = getIntent();
		mProduct = ( Product ) intent.getSerializableExtra( "Product" );

		sActionBar.setHomeButtonEnabled( true );
		sActionBar.setDisplayHomeAsUpEnabled( true );
		sActionBar.setTitle( mProduct.getName() );

		TextView tvProductName = ( TextView ) findViewById( R.id.tv_product_name );

		tvProductName.setText( mProduct.getPrice() );
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
