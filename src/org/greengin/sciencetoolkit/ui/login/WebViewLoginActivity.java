package org.greengin.sciencetoolkit.ui.login;

import org.greengin.sciencetoolkit.logic.remote.RemoteApi;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewLoginActivity extends ActionBarActivity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				setProgress(progress * 100);
			}
		});
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setTitle(url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				CookieSyncManager.getInstance().sync();
				// Get the cookie from cookie jar.
				String cookie = CookieManager.getInstance().getCookie(url);
				if (cookie == null) {
					return;
				}
				// Cookie is a string like NAME=VALUE [; NAME=VALUE]
				String[] pairs = cookie.split(";");
				for (int i = 0; i < pairs.length; ++i) {
					String[] parts = pairs[i].split("=", 2);
					// If token is found, return it to the calling activity.
					if (parts.length == 2 && parts[0].equalsIgnoreCase("JSESSIONID")) {
						RemoteApi.get().setSession(parts[1]);
						//finish();
					}
				}
			}
		});

		setContentView(webview);

		webview.loadUrl("http://pontos.open.ac.uk/sense-it-web/login/redirect?p=google");
	}


	@Override
	protected void onPause() {
		super.onPause();
		CookieSyncManager.getInstance().stopSync();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CookieSyncManager.getInstance().startSync();
	}

}
