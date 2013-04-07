package me.javierferrer.dailyoffersapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.Product;
import me.javierferrer.dailyoffersapp.models.ProductsList;
import me.javierferrer.dailyoffersapp.utils.ProductsAdapter;

public final class FavoritedProductsActivity extends ProductsListBaseActivity
{

	private static ProductsAdapter sFavoritedProductsAdapter;

	protected final String mClassName = this.getClass().getSimpleName();

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		Log.d( TAG, mClassName + "\t" + "onCreate" );

		ListView productsListView = ( ListView ) sProductsListBaseActivity.findViewById( R.id.products_list );

		sFavoritedProductsAdapter =
				new ProductsAdapter( sProductsListBaseActivity.getApplicationContext(), R.layout.products_list_entry,
						ProductsList.getInstance().getFavoritedProducts() );

		productsListView.setAdapter( sFavoritedProductsAdapter );
	}

	/**
	 * On start method overridden in order to hide search view text input field
	 */
	@Override
	public void onStart()
	{
		super.onStart();

		Log.d( TAG, mClassName + "\t" + "onStart" );

		// If we have previously set the search menu item, make sure it's collapsed (it's possible to come here from
		// search results, in that case, we have to close the SearchView text input)
		if ( mSearchMenuItem != null )
		{
			mSearchMenuItem.collapseActionView();
		}
	}

	public static void setFavoriteProduct( Product selectedProduct, boolean newStatus )
	{
		// If we've already initialized the favorited products adapter, add or remove the modified product
		if ( sFavoritedProductsAdapter != null )
		{
			// Add/remove it from the favorited products adapter
			if ( newStatus )
			{
				sFavoritedProductsAdapter.add( selectedProduct );
			}
			else
			{
				sFavoritedProductsAdapter.remove( selectedProduct );
			}
		}
	}

	/******************************************************************************************************
	 * Action Bar (search, bookmarks and settings)
	 *****************************************************************************************************/

	/**
	 * Override options menu creation in order to delete My Favorites menu item
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		super.onCreateOptionsMenu( menu );

		MenuItem moreMenuItem = menu.findItem( R.id.mi_more ); // Get my "More" menu item
		SubMenu moreSubMenu = moreMenuItem.getSubMenu(); // Get the submenu contained in the "More" item
		moreSubMenu.removeItem( R.id.mi_favorites_list ); // Delete My Favorites submenu item

		return true;
	}
}
