package org.greengin.sciencetoolkit.common.ui.base;

import org.greengin.sciencetoolkit.common.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.common.logic.appstatus.ApplicationStatusActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
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
			private void checkPage(String url) {
				if (url.contains(RemoteApi.WELCOME_PATH)) {
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
						if (parts.length == 2 && "JSESSIONID".equalsIgnoreCase(parts[0].trim())) {
							RemoteApi.get().setSession(parts[1].trim());
							finish();
						}
					}
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setTitle(url);
				checkPage(url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				checkPage(url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCod,
					String description, String failingUrl) {
				RemoteApi.get().loginActionComplete();
			}
		});

		webview.loadUrl(RemoteApi.PROTOCOL + "://" + RemoteApi.DOMAIN
				+ RemoteApi.PATH + "social/google/login");

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
