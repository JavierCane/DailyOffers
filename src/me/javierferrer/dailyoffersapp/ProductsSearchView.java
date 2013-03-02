package me.javierferrer.dailyoffersapp;

import android.content.Context;
import com.actionbarsherlock.widget.SearchView;

/**
 * Custom SearchView
 * Needed in order to call to ProductsListActivity.showTabs method when the user goes out the text edit and show again the layout tabs
 */
public class ProductsSearchView extends SearchView
{

	public ProductsSearchView( Context context )
	{
		super( context );
	}

	@Override
	public void onActionViewCollapsed()
	{
		super.onActionViewCollapsed();
		ProductsListActivity.showTabs();
	}
}
