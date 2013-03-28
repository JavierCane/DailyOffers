package me.javierferrer.dailyoffersapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import me.javierferrer.dailyoffersapp.R;

public class WebViewActivity extends Activity
{

	private WebView mWebView;

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.webview );

		Bundle bundle = getIntent().getExtras();
		String url = bundle.getString( "url" );

		mWebView = ( WebView ) findViewById( R.id.web_view );
		mWebView.getSettings().setJavaScriptEnabled( true );
		mWebView.loadUrl( url );
	}
}
