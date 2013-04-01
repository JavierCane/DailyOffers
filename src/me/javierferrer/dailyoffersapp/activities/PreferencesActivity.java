package me.javierferrer.dailyoffersapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import me.javierferrer.dailyoffersapp.R;

/**
 * Created with IntelliJ IDEA.
 * User: javierferrer
 * Date: 05/03/13
 * Time: 21:17
 * To change this template use File | Settings | File Templates.
 */
public final class PreferencesActivity extends SherlockPreferenceActivity
{

	/**
	 * Set a preferences change listener in order to redraw application tabs when the user
	 * changes the categories visibility
	 */
	private final SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener =
			new SharedPreferences.OnSharedPreferenceChangeListener()
			{

				public void onSharedPreferenceChanged( SharedPreferences preferences, String key )
				{
					ProductsListBaseActivity.setCategoriesVisibilityChanged( true );
				}
			};

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		getSupportActionBar().setHomeButtonEnabled( true );
		getSupportActionBar().setDisplayHomeAsUpEnabled( true );

		addPreferencesFromResource( R.xml.preferences );

		// Set preferences listener in order to redraw application tabs when the user changes the categories visibility
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		preferences.registerOnSharedPreferenceChangeListener( mPreferenceChangeListener );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case android.R.id.home:
				Intent intent = new Intent( this, ProductsByCategoryActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );
			default:
				return super.onOptionsItemSelected( item );
		}
	}
}
