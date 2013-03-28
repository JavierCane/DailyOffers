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
public class ProductDetailsActivity extends SherlockActivity
{
	private static ActionBar sActionBar;
	private static Product sProduct;

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.product_details );

		sActionBar = getSupportActionBar();
		Intent intent = getIntent();
		sProduct = ( Product ) intent.getSerializableExtra( "Product" );

		sActionBar.setHomeButtonEnabled( true );
		sActionBar.setDisplayHomeAsUpEnabled( true );
		sActionBar.setTitle( sProduct.getName() );

		TextView tv_product_name = ( TextView ) findViewById( R.id.tv_product_name );

		tv_product_name.setText( sProduct.getPrice() );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case android.R.id.home:
				finish();
			default:
				return super.onOptionsItemSelected( item );
		}
	}
}
