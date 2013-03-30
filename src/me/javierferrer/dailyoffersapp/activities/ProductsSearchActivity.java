package me.javierferrer.dailyoffersapp.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.ProductsList;
import me.javierferrer.dailyoffersapp.utils.ProductsAdapter;

public final class ProductsSearchActivity extends ProductsListBaseActivity
{

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		hideTabs();
	}

	@Override
	protected void handleIntent( Intent intent )
	{
		if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) )
		{
			Log.d( TAG, "ProductsSearchActivity: handleIntent: search intent detected" );

			String query = intent.getStringExtra( SearchManager.QUERY );

			sActionBar.setTitle( getResources().getString( R.string.search_results ) + ": \"" + query + "\"" );

			searchProducts( query );
		}
		else
		{
			Log.e( TAG, "ProductsSearchActivity: handleIntent: Not expected intent: " + intent.getAction() );
		}
	}

	/**
	 * ***************************************************************************************************
	 * Products list
	 * ***************************************************************************************************
	 */

	private void searchProducts( String query )
	{
		Log.d( TAG, "ProductsSearchActivity: searchProducts: search intent, query: " + query );

		// Get the xml/preferences.xml preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );

		// Check if the user has specified that the categories filters affects on search results
		ProductsAdapter productsAdapter;
		if ( preferences.getBoolean( "visibility_affects_search", true ) )
		{
			productsAdapter = new ProductsAdapter( this, R.layout.products_list_entry,
					ProductsList.getInstance().getFilteredProducts( query, mVisibleCategories ) );
		}
		else
		{
			productsAdapter = new ProductsAdapter( this, R.layout.products_list_entry,
					ProductsList.getInstance().getFilteredProducts( query, CATEGORIES ) );
		}

		sProductsListView.setAdapter( productsAdapter );
		sProductsListView.setVisibility( ListView.VISIBLE ); // TODO: Necesario?
	}
}