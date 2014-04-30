package org.greengin.sciencetoolkit.ui.remote;

import org.greengin.sciencetoolkit.logic.appstatus.ApplicationStatusActivity;
import org.greengin.sciencetoolkit.logic.remote.RemoteApi;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewLoginActivity extends ApplicationStatusActivity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		WebView webview = new WebView(this);

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
				if (url.endsWith(RemoteApi.WELCOME_PATH_SUFFIX)) {
					Log.d("stk webview", "page finished: " + url);

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
							Log.d("stk webview", "jsession received");
							RemoteApi.get().setSession(parts[1]);
							finish();
							Log.d("stk webview", "finished");
						}
					}
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCod, String description, String failingUrl) {
			}
		});

		webview.loadUrl(RemoteApi.PROTOCOL + "://" + RemoteApi.DOMAIN + RemoteApi.PATH + "social/google/login");

		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		setContentView(webview);
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
