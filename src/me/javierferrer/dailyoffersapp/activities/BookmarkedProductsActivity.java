package me.javierferrer.dailyoffersapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.Product;
import me.javierferrer.dailyoffersapp.models.ProductsList;
import me.javierferrer.dailyoffersapp.utils.ProductsAdapter;

public final class BookmarkedProductsActivity extends ProductsListBaseActivity
{

	protected final String mClassName = this.getClass().getSimpleName();

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		Log.d( TAG, mClassName + "\t" + "onCreate" );

		showBookmarks();
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

	/******************************************************************************************************
	 * Products list
	 *****************************************************************************************************/

	/**
	 * Show bookmarked products
	 */
	private void showBookmarks()
	{
		Log.d( TAG, mClassName + "\t" + "showBookmarks" );

		sProductsListView.setAdapter( new ProductsAdapter( this, R.layout.products_list_entry,
				ProductsList.getInstance().getBookmarkedProducts() ) );
	}

	/**
	 * Override context item option selected in order to redraw the products list in case of removing a product from
	 * the bookmarks
	 *
	 * @param item
	 * @return
	 */
	@Override
	public boolean onContextItemSelected( android.view.MenuItem item )
	{
		AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) item.getMenuInfo();

		Product selectedProduct = ( Product ) sProductsListView.getItemAtPosition( info.position );

		switch ( item.getItemId() )
		{
			// In case of add/remove from bookmarks menu item click
			case R.id.mi_bookmark_product:
				// Set the product bookmarked flag to the opposite of the current value
				selectedProduct.setBookmarked( !selectedProduct.isBookmarked() );

				// Add/remove the selected product to the bookmarked products list
				ProductsList.getInstance().setBookmarkedProduct( this.getApplicationContext(), selectedProduct.getId(),
						selectedProduct.isBookmarked() );

				showBookmarks();

				return true;
			default:
				return super.onContextItemSelected( item );
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

		MenuItem moreMenuItem = menu.findItem( R.id.mi_more ); // Get my "More" menu item
		SubMenu moreSubMenu = moreMenuItem.getSubMenu(); // Get the submenu contained in the "More" item
		moreSubMenu.removeItem( R.id.mi_bookmarks_list ); // Delete My Bookmarks submenu item

		return true;
	}
}
