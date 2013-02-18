package me.javierferrer.dailyoffersapp;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends Activity
{

	private WebView webView;

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.webview );

		Bundle bundle = getIntent().getExtras();
		String url = bundle.getString( "url" );

		webView = ( WebView ) findViewById( R.id.web_view );
		webView.getSettings().setJavaScriptEnabled( true );
		webView.loadUrl( url );
	}
}
