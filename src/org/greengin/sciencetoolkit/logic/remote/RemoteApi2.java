package org.greengin.sciencetoolkit.logic.remote;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.greengin.sciencetoolkit.ui.login.WebViewLoginActivity;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.CookieSyncManager;

public class RemoteApi2 {

	private static RemoteApi2 instance;

	public static RemoteApi2 get() {
		return instance;
	}

	public static void init(Context applicationContext) {
		RemoteApi2.instance = new RemoteApi2(applicationContext);
	}

	Context applicationContext;
	boolean logged;
	DefaultHttpClient httpClient;

	private RemoteApi2(Context applicationContext) {
		this.applicationContext = applicationContext;
		this.httpClient = new DefaultHttpClient();
		this.logged = false;

		CookieSyncManager.createInstance(applicationContext);
	}

	public void request(RemoteCapableActivity activity, RemoteAction action, boolean afterLoginAttempt) {
		if (logged) {
			execute(action);
		} else if (!afterLoginAttempt) {
			activity.remoteSetOnResumeAction(action);
			tryToLogin(activity);
		}
	}

	private void execute(RemoteAction action) {
		new ActionThread(action).start();
	}

	public void setSession(String session) {
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", session);
		cookie.setDomain("pontos.open.ac.uk");
		cookie.setPath("/sense-it-web/");

		httpClient.getCookieStore().addCookie(cookie);

		new ActionThread(new LoginAction(true)).start();
	}

	public void logout() {
		if (logged) {
			new ActionThread(new LoginAction(false)).start();
		}
	}
	
	public void tryToLogin(RemoteCapableActivity activity) {
		Log.d("stk remote", "login requested");
		Intent intent = new Intent(activity, WebViewLoginActivity.class);
		activity.startActivity(intent);
	}

	private class ActionThread extends Thread {

		RemoteAction action;

		public ActionThread(RemoteAction action) {
			this.action = action;
		}

		public void run() {
			try {
				HttpRequestBase[] requests = action.createRequests("http://pontos.open.ac.uk/sense-it-web/api/");

				for (int i = 0; i < requests.length; i++) {
					action.aboutToRun(i);
					HttpResponse response = httpClient.execute(requests[i]);

					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					String line;
					StringBuffer answer = new StringBuffer();
					while ((line = reader.readLine()) != null) {
						answer.append(line).append('\n');
					}
					reader.close();
					response.getEntity().consumeContent();

					action.result(i, answer.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private class LoginAction extends RemoteJsonAction {
		
		boolean login;
		
		public LoginAction(boolean login) {
			this.login = login;
		}
		
		@Override
		public HttpRequestBase[] createRequests(String urlBase) {
			String url = String.format("%sopenid/%s", urlBase, login ? "profile" : "logout");
			return new HttpRequestBase[] { new HttpGet(url) };
		}

		@Override
		public void result(int request, JSONObject result) {
			try {
				logged = result.getBoolean("logged");
			} catch (Exception e) {
				logged = false;
			}

			Log.d("stk remote", "logged: " + logged);
		}
	}


}
