package me.javierferrer.dailyoffersapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import me.javierferrer.dailyoffersapp.R;

public final class WebViewActivity extends Activity
{

	private WebView mWebView;

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.webview );

		Bundle bundle = getIntent().getExtras();
		String url = bundle.getString( "url" );

		mWebView = ( WebView ) findViewById( R.id.web_view );
		// Set a custom webView client in order to load the URL in this activity instead of launching another one
		mWebView.setWebChromeClient( new WebChromeClient() );
		mWebView.setWebViewClient( new MyWebViewClient() );
		mWebView.getSettings().setJavaScriptEnabled( true );

		mWebView.loadUrl( url );
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		// Check if the key event was the Back button and if there's history
		if ( ( keyCode == KeyEvent.KEYCODE_BACK ) && mWebView.canGoBack() )
		{
			mWebView.goBack();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown( keyCode, event );
	}

	private class MyWebViewClient extends WebViewClient
	{

		@Override
		public boolean shouldOverrideUrlLoading( WebView view, String url )
		{
			// TODO: Capturar URL de compra final y registrar evento
			view.loadUrl( url );
			return true;
		}
	}
}
