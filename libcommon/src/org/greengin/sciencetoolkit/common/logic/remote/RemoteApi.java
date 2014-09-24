package org.greengin.sciencetoolkit.common.logic.remote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.greengin.sciencetoolkit.common.ui.base.RemoteCapableActivity;
import org.greengin.sciencetoolkit.common.ui.base.WebViewLoginActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.CookieSyncManager;

public class RemoteApi {

	public static final String REMOTE_LOGIN_EVENT_FILTER = "REMOTE_EVENT_FILTER";

	public static final String PROTOCOL = "http";
	public static final String DOMAIN = "www.nquire-it.org";
	public static final String PATH = "/";
	public static final String WELCOME_PATH_SUFFIX = "welcome";

	private static RemoteApi instance;

	public static RemoteApi get() {
		return instance;
	}

	public static void init(Context applicationContext) {
		RemoteApi.instance = new RemoteApi(applicationContext);
	}

	Context applicationContext;
	boolean logged;
	String token;
	String username;
	DefaultHttpClient httpClient;

	Vector<RemoteAction> queue;
	boolean running;
	ReentrantLock runningLock;

	public String getUsername() {
		return username;
	}

	public boolean isLogged() {
		return logged;
	}

	private RemoteApi(Context applicationContext) {
		this.applicationContext = applicationContext;
		this.httpClient = new DefaultHttpClient();
		this.logged = false;

		queue = new Vector<RemoteAction>();
		running = false;
		runningLock = new ReentrantLock();

		CookieSyncManager.createInstance(applicationContext);
	}

	public void request(RemoteCapableActivity activity, RemoteAction action) {
		Log.d("stk remote", "request " + action.getClass().getSimpleName());
		Log.d("stk remote", "request " + logged + " " + running);
		
		queue.add(action);
		if (logged) {
			if (!running) {
				executeOne();
			}
		} else {
			tryToLogin(activity);
		}
	}

	public void resumeRequests(RemoteCapableActivity activity) {
		Log.d("stk remote", "resume requests " + activity.getClass().getName());

		if (queue.size() > 0 && !running) {
			if (logged) {
				executeOne();
			}
		} 
	}
	
	public void loginActionComplete() {
		if (!logged) {
			for (RemoteAction action : queue) {
				action.error(-1, "nologin");
			}
			queue.clear();
		}
	}

	private void executeOne() {
		Log.d("stk remote", "execute one");

		try {
			running = true;
			RemoteAction action = queue.remove(0);
			new ActionThread(action).start();
		} catch (Exception e) {
			running = false;
		}
	}

	public void setSession(String session) {
		Log.d("stk remote", "set session");
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", session);
		cookie.setDomain(DOMAIN);
		cookie.setPath(PATH);

		httpClient.getCookieStore().addCookie(cookie);

		new ActionThread(new GetTokenAction(true)).start();
	}

	public void logout() {
		if (logged) {
			new ActionThread(new GetTokenAction(false)).start();
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
			Log.d("stk remote", "start: " + action.getClass().getSimpleName());
			try {
				HttpRequestBase[] requests = action.createRequests("http://" + DOMAIN + PATH + "api/");

				for (int i = 0; i < requests.length; i++) {
					requests[i].addHeader("nquire-it-token", token);
					action.aboutToRun(i);
					try {
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
					} catch (Exception e) {
						e.printStackTrace();
						action.error(i, e.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d("stk remote", "end: " + action.getClass().getSimpleName());

			executeOne();
		}
	}

	private class GetTokenAction extends RemoteJsonAction {

		boolean login;

		public GetTokenAction(boolean login) {
			this.login = login;
		}

		@Override
		public HttpRequestBase[] createRequests(String urlBase) {

			HttpRequestBase request = login ? new HttpGet(urlBase + "security/status") : new HttpPost(urlBase + "security/logout");

			return new HttpRequestBase[] { request };
		}

		@Override
		public void result(int request, JSONObject result, JSONArray array) {
			try {
				token = result.getString("token");
				logged = result.getBoolean("logged");
				username = logged ? result.getJSONObject("profile").getString("username") : null;
			} catch (Exception e) {
				logged = false;
				token = null;
				username = null;
			}

			Log.d("stk remote", "logged: " + logged + " " + token);
			Log.d("stk remote", result.toString());

			Intent i = new Intent(REMOTE_LOGIN_EVENT_FILTER);
			LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i);
			
			loginActionComplete();
		}
		
		@Override 
		public void error(int request, String msg) {
			loginActionComplete();
		}
	}

}
