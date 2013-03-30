package me.javierferrer.dailyoffersapp.widgets;

import android.content.Context;
import android.util.Log;
import com.actionbarsherlock.widget.SearchView;
import me.javierferrer.dailyoffersapp.activities.ProductsListBaseActivity;

/**
 * Custom SearchView
 * Needed in order to call to ProductsListBaseActivity.showTabs method when the user goes out the text edit and show again the layout tabs
 */
public final class ProductsSearchView extends SearchView
{

	public ProductsSearchView( Context context )
	{
		super( context );

		Log.d( ProductsListBaseActivity.TAG, "ProductsSearchView: ProductsSearchView constructor" );
	}

	@Override
	public void onActionViewCollapsed()
	{
		Log.d( ProductsListBaseActivity.TAG, "ProductsSearchView: onActionViewCollapsed" );

		super.onActionViewCollapsed();
//		ProductsListBaseActivity.showTabs(); // TODO: Hack
	}
}
