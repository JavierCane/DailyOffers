package me.javierferrer.dailyoffersapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.ProductsList;
import me.javierferrer.dailyoffersapp.utils.ProductsAdapter;

import static com.actionbarsherlock.app.ActionBar.Tab;

public final class ProductsByCategoryActivity extends ProductsListBaseActivity implements ActionBar.TabListener
{

	protected final String mClassName = this.getClass().getSimpleName();

	private static ProductsByCategoryActivity sProductsByCategoryActivity;
	private static Tab sTab;
	private FragmentTransaction sTransaction;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		// We have to initialize sProductsByCategoryActivity in order to make it accessible when
		// we receive the productsParseCallback method callback
		sProductsByCategoryActivity = this;

		super.onCreate( savedInstanceState );

		Log.d( TAG, mClassName + "\t" + "onCreate" );

		setNavigationWithTabs();
	}

	/**
	 * On start method overridden in order to re-initialize tabs when preferences has been changed
	 * This is because the user could be set as hidden/shown some categories
	 */
	@Override
	public void onStart()
	{
		super.onStart();

		Log.d( TAG, mClassName + "\t" + "onStart" );

		// Re-initialize tabs (preferences probably has been changed)
		initVisibleCategories();

		// If we have previously set the search menu item, make sure it's collapsed (it's possible to come here from
		// search results, in that case, we have to close the SearchView text input)
		if ( mSearchMenuItem != null )
		{
			mSearchMenuItem.collapseActionView();
		}
	}

	@Override
	protected void handleIntent( Intent intent )
	{
		if ( Intent.ACTION_MAIN.equals( intent.getAction() ) )
		{
			Log.d( TAG, mClassName + "\t" + "handleIntent: main action intent detected" );

			if ( ProductsList.getInstance().isLoaded() )
			{
				productsParseCallback();
			}
		}
		else
		{
			Log.e( TAG, mClassName + "\t" + "handleIntent: Not expected intent: " + intent.getAction() );
		}
	}

	/******************************************************************************************************
	 * Products list
	 *****************************************************************************************************/

	/**
	 * Method called when ProductsList has finished the products parsing process.
	 * It's necessary because it's possible to call to fillCategoryTab method without having finished the parsing process.
	 */
	public static void productsParseCallback()
	{
		Log.d( TAG, "ProductsByCategoryActivity" + "\t" + "productsParseCallback" );

		// If we have any tab at the Action Bar and we have not set any one yet or the current set tab is not marked as visible,
		// change the selected tab to the first one
		if ( sActionBar.getTabCount() > 0 )
		{
			if ( sTab == null )
			{
				sTab = sActionBar.getTabAt( 0 );
			}

			ListView productsListView = ( ListView ) sProductsListBaseActivity.findViewById( R.id.products_list );

			productsListView.setAdapter( new ProductsAdapter( sProductsListBaseActivity.getApplicationContext(),
					R.layout.products_list_entry,
					ProductsList.getInstance().getCategoryProductsList( sTab.getTag().toString() ) ) );
		}
	}

	/******************************************************************************************************
	 * Categories & tabs
	 *****************************************************************************************************/

	/**
	 * Construct categories tabs
	 */
	@Override
	protected void initVisibleCategories()
	{
		super.initVisibleCategories();

		Log.d( TAG, mClassName + "\t" + "initVisibleCategories" );

		// If the user has not set any visible category in settings, inform about it
		if ( mVisibleCategories.isEmpty() )
		{
			mProductsListView.setVisibility( View.INVISIBLE );
			Toast.makeText( getApplicationContext(),
					getResources().getString( R.string.no_visible_categories_explanation ), Toast.LENGTH_LONG ).show();
		}
		else
		{
			mProductsListView.setVisibility( View.VISIBLE );
		}

		initVisibleTabs();
	}

	/**
	 * Method used in order to initialize ActionBar tabs thanks to the previously set visible categories
	 */
	private void initVisibleTabs()
	{
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
		if ( sActionBar.getTabCount() > 0 )
		{
			if ( sTab == null || !mVisibleCategories.contains( sTab.getTag() ) )
			{
				sTab = sActionBar.getTabAt( 0 );
			}
		}
		else
		{
			setNavigationWithoutTabs( false );
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
		if ( sTab != null )
		{
			Log.d( TAG, mClassName + "\t" + "onTabSelected: previous tab: " + sTab.getTag() +
			            ", current tab: " + tab.getTag() );
		}
		else
		{
			Log.d( TAG, mClassName + "\t" + "onTabSelected: previous tab: 'null', current tab: " + tab.getTag() );
		}

		if ( ProductsList.getInstance().isLoaded() )
		{
			ListView productsListView = ( ListView ) findViewById( R.id.products_list );

			productsListView.setAdapter( new ProductsAdapter( this, R.layout.products_list_entry,
					ProductsList.getInstance().getCategoryProductsList( tab.getTag().toString() ) ) );
		}
		else
		{
			Log.d( TAG, mClassName + "\t" + "onTabSelected: Products list not parsed yet." );
		}

		this.sTab = tab;
		this.sTransaction = transaction;
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
