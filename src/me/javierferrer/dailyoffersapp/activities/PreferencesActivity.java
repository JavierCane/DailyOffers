package me.javierferrer.dailyoffersapp.activities;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import me.javierferrer.dailyoffersapp.R;

/**
 * Created with IntelliJ IDEA.
 * User: javierferrer
 * Date: 05/03/13
 * Time: 21:17
 * To change this template use File | Settings | File Templates.
 */
public class PreferencesActivity extends SherlockPreferenceActivity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		getSupportActionBar().setHomeButtonEnabled( true );
		getSupportActionBar().setDisplayHomeAsUpEnabled( true );

		addPreferencesFromResource( R.xml.preferences );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case android.R.id.home:
				finish();
			default:
				return super.onOptionsItemSelected( item );
		}
	}
}
