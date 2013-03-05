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
	private static ActionBar action_bar;
	private static Product product;

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.product_details );

		action_bar = getSupportActionBar();
		Intent intent = getIntent();
		product = ( Product ) intent.getSerializableExtra( "Product" );

		action_bar.setHomeButtonEnabled( true );
		action_bar.setDisplayHomeAsUpEnabled( true );
		action_bar.setTitle( product.getName() );

		TextView tv_product_name = ( TextView ) findViewById( R.id.tv_product_name );

		tv_product_name.setText( product.getPrice() );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent( this, ProductsListActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );
				return true;
			default:
				return super.onOptionsItemSelected( item );
		}
	}
}
