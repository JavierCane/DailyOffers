package me.javierferrer.dailyoffersapp.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.ProductsList;
import me.javierferrer.dailyoffersapp.utils.ProductsAdapter;

// TODO: No sugerencias
// TODO: No búsquedas previas
// TODO: Botón físico "buscar"
// TODO: En emulador no muestra botón "More"
// TODO:
public final class ProductsSearchActivity extends ProductsListBaseActivity
{

	private String mQuery;

	protected final String mClassName = this.getClass().getSimpleName();

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		Log.d( TAG, mClassName + "\t\t" + "onCreate" );
	}

	/**
	 * Override onNewIntent in order to launch new search queries from the same activity ("singleTop" launchMode)
	 *
	 * @param intent
	 */
	@Override
	protected void onNewIntent( Intent intent )
	{
		Log.d( TAG, mClassName + "\t\t" + "onNewIntent: " + intent.toString() );

		setIntent( intent );
		handleIntent( intent );
	}

	@Override
	protected void handleIntent( Intent intent )
	{
		if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) )
		{
			Log.d( TAG, mClassName + "\t\t" + "handleIntent: search intent detected" );

			mQuery = intent.getStringExtra( SearchManager.QUERY );

			sActionBar.setTitle( getResources().getString( R.string.search_results ) + ": \"" + mQuery + "\"" );

			searchProducts();
		}
		else
		{
			Log.e( TAG, mClassName + "\t\t" + "handleIntent: Not expected intent: " + intent.getAction() );
		}
	}

	/*****************************************************************************************************
	 * Products list
	 ****************************************************************************************************/

	/**
	 * Search products by name using the mQuery class attribute.
	 * It takes into account if the user has specified some hidden categories and if it affects to the search scope.
	 */
	private void searchProducts()
	{
		Log.d( TAG, mClassName + "\t\t" + "searchProducts: search intent, query: " + mQuery );

		// Get the xml/preferences.xml preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );

		ListView productsListView = ( ListView ) findViewById( R.id.products_list );

		// Check if the user has specified that the categories filters affects on search results
		if ( preferences.getBoolean( "visibility_affects_search", true ) )
		{
			productsListView.setAdapter( new ProductsAdapter( this, R.layout.products_list_entry,
					ProductsList.getInstance().getFilteredProducts( mQuery, mVisibleCategories ) ) );
		}
		else
		{
			productsListView.setAdapter( new ProductsAdapter( this, R.layout.products_list_entry,
					ProductsList.getInstance().getFilteredProducts( mQuery, CATEGORIES ) ) );
		}
	}

	/******************************************************************************************************
	 * Action Bar (search, bookmarks and settings)
	 *****************************************************************************************************/

	/**
	 * Override options menu creation in order to delete My Bookmarks menu item
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		super.onCreateOptionsMenu( menu );

		// TODO: En emulador no muestra botón "More"

		// Set showAsAction property to "always" in order to be able to setIconifiedByDefault to false
		mSearchMenuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );

		// Do not iconify the search view widget in order to make it more accesible
		mSearchView.setIconifiedByDefault( false );

		// Show the current query as the search input text
		mSearchView.setQuery( mQuery, false );

		return true;
	}
}
