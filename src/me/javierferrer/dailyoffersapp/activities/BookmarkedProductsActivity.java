package me.javierferrer.dailyoffersapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import me.javierferrer.dailyoffersapp.R;
import me.javierferrer.dailyoffersapp.models.Product;
import me.javierferrer.dailyoffersapp.models.ProductsList;
import me.javierferrer.dailyoffersapp.utils.ProductsAdapter;

public final class BookmarkedProductsActivity extends ProductsListBaseActivity
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
		Log.d( TAG, "BookmarkedProductsActivity: handleIntent: " + intent.getAction() );

		showBookmarks();
	}

	/******************************************************************************************************
	 * Products list
	 *****************************************************************************************************/

	/**
	 * Show bookmarked products
	 */
	private void showBookmarks()
	{
		Log.d( TAG, "BookmarkedProductsActivity: showBookmarks" );

		if ( ProductsList.getInstance().isLoaded() )
		{
			sProductsListView.setAdapter( new ProductsAdapter( this, R.layout.products_list_entry,
					ProductsList.getInstance().getBookmarkedProducts() ) );
			sProductsListView.setVisibility( ListView.VISIBLE );
		}
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

		MenuItem moreMenuItem = menu.findItem( R.id.mi_more ); // get my MenuItem with placeholder submenu
		SubMenu moreSubMenu = moreMenuItem.getSubMenu();
		moreSubMenu.removeItem( R.id.mi_bookmarks_list ); // delete place holder

		return true;
	}
}
