package me.javierferrer.dailyoffersapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.Product;
import me.javierferrer.dailyoffersapp.models.ProductsList;
import me.javierferrer.dailyoffersapp.utils.ProductsAdapter;

import java.util.ArrayList;

import static com.actionbarsherlock.app.ActionBar.Tab;

public final class ProductsByCategoryActivity extends ProductsListBaseActivity implements ActionBar.TabListener
{

	private static ProductsByCategoryActivity sProductsByCategoryActivity;
	private static Tab sTab;
	private FragmentTransaction sTransaction;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		// We have to initialize sProductsByCategoryActivity in order to make it accessible when
		// we receive the productsParseCompleted method callback
		sProductsByCategoryActivity = this;

		super.onCreate( savedInstanceState );

		initTabs();
		showTabs();
	}

	@Override
	protected void handleIntent( Intent intent )
	{
		if ( Intent.ACTION_MAIN.equals( intent.getAction() ) )
		{
			Log.d( TAG, "ProductsByCategoryActivity: handleIntent: main action intent detected" );

			if ( ProductsList.getInstance().isLoaded() )
			{
				productsParseCompleted();
			}
		}
		else
		{
			Log.e( TAG, "ProductsSearchActivity: handleIntent: Not expected intent: " + intent.toString() );
		}
	}

	/**
	 * On start method overrided in order to re-initialize tabs when preferences has been changed
	 * This is because the user could be set as hidden/shown some categories
	 */
	@Override
	public void onStart()
	{
		Log.d( TAG, "ProductsByCategoryActivity: onStart" );
		super.onStart();

		// Construct tabs
		initTabs();
	}

	/******************************************************************************************************
	 * Product list
	 *****************************************************************************************************/

	/**
	 * Method called when ProductsList has finished the products parsing process.
	 * It's necessary because it's possible to call to fillCategoryTab method without having finished the parsing process.
	 */
	public static void productsParseCompleted()
	{
		Log.d( TAG, "ProductsByCategoryActivity: productsParseCompleted" );

		// If we have any tab at the Action Bar and we have not set any one yet or the current set tab is not marked as visible,
		// change the selected tab to the first one
		if ( sActionBar.getTabCount() > 0 )
		{
			if ( sTab == null )
			{
				sTab = sActionBar.getTabAt( 0 );
			}

			int l = R.layout.products_list_entry;
			Object ta = sTab.getTag();
			String t = ta.toString();
			ArrayList<Product> pe = ProductsList.getInstance().getCategoryProductsList( t );

			ProductsAdapter productsAdapter = new ProductsAdapter( sProductsListBaseActivity, l, pe );
			sProductsListView.setAdapter( productsAdapter );
			sProductsListView.setVisibility( ListView.VISIBLE );
		}
	}

	/******************************************************************************************************
	 * Categories & tabs
	 *****************************************************************************************************/

	/**
	 * Construct categories tabs
	 */
	private void initTabs()
	{
		Log.d( TAG, "ProductsByCategoryActivity: initTabs" );

		sActionBar.removeAllTabs();

		for ( String categoryName : mVisibleCategories )
		{
			Tab tab = sActionBar.newTab();
			tab.setText( categoryName );
			tab.setTag( categoryName );
			tab.setTabListener( sProductsByCategoryActivity );
			sActionBar.addTab( tab );
		}

		// If we have any tab at the Action Bar and we have not set any one yet or the current set tab is not marked as visible,
		// change the selected tab to the first one
		if ( sActionBar.getTabCount() > 0 && ( sTab == null || !mVisibleCategories.contains( sTab.getTag() ) ) )
		{
			sTab = sActionBar.getTabAt( 0 );
		}
	}

	/**
	 * Mandatory override
	 *
	 * @param tab         The sTab that was reselected.
	 * @param transaction
	 */
	@Override
	public void onTabReselected( Tab tab, FragmentTransaction transaction )
	{
		this.sTab = tab;
		this.sTransaction = transaction;
	}

	/**
	 * Tab change event listener
	 * fills the current category sTab with the required products
	 *
	 * @param tab         The sTab that was selected
	 * @param transaction
	 */
	@Override
	public void onTabSelected( Tab tab, FragmentTransaction transaction )
	{
		this.sTab = tab;
		this.sTransaction = transaction;

		if ( ProductsList.getInstance().isLoaded() )
		{
			ProductsAdapter productsAdapter =
					new ProductsAdapter( this, R.layout.products_list_entry, ProductsList.getInstance().getCategoryProductsList( tab.getTag().toString() ) );
			sProductsListView.setAdapter( productsAdapter );
			sProductsListView.setVisibility( ListView.VISIBLE );
		}
	}

	/**
	 * Mandatory override
	 *
	 * @param tab         The sTab that was unselected
	 * @param transaction
	 */
	@Override
	public void onTabUnselected( Tab tab, FragmentTransaction transaction )
	{
		this.sTab = tab;
		this.sTransaction = transaction;
	}
}
