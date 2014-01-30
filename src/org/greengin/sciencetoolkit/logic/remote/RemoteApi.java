package org.greengin.sciencetoolkit.logic.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class RemoteApi implements ModelNotificationListener {
	static final String REMOTE_LOGIN_URL = "https://nquireprojects.appspot.com/_ah/login?auth=";
	static final String REMOTE_REQUEST_URL = "https://nquireprojects.appspot.com/api/";

	private static RemoteApi instance;

	public static RemoteApi get() {
		return instance;
	}

	public static void init(Context applicationContext) {
		RemoteApi.instance = new RemoteApi(applicationContext);
	}

	AccountManager accountManager;
	Context applicationContext;
	String accountName;
	Account account;
	DefaultHttpClient http_client;;

	private RemoteApi(Context applicationContext) {
		this.applicationContext = applicationContext;
		this.accountManager = AccountManager.get(applicationContext);
		this.http_client = new DefaultHttpClient();
		this.account = null;

		SettingsManager.get().registerDirectListener("app", this);
		updateCurrentAccount();
	}

	@Override
	public void modelNotificationReceived(String msg) {
		updateCurrentAccount();
	}

	private void updateCurrentAccount() {
		Account newAccount = null;
		this.accountName = SettingsManager.get().get("app").getString("account");
		for (Account a : availableAccounts()) {
			if (a.name.equals(this.accountName)) {
				newAccount = a;
				break;
			}
		}

		if (newAccount != this.account) {
			this.account = newAccount;
		}
	}

	public Account[] availableAccounts() {
		return this.accountManager.getAccountsByType("com.google");
	}

	public void request(RemoteCapableActivity activity, RemoteAction action) {
		getAuthToken(activity, action);
	}

	private void getAuthToken(RemoteCapableActivity activity, RemoteAction action) {
		if (this.account != null) {
			accountManager.getAuthToken(this.account, "ah", null, activity, new GetAuthTokenCallback(activity, action), null);
		}
	}

	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {
		RemoteCapableActivity activity;
		RemoteAction action;

		public GetAuthTokenCallback(RemoteCapableActivity activity, RemoteAction action) {
			this.activity = activity;
			this.action = action;
		}

		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle;
			try {
				bundle = result.getResult();
				Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					// User input required
					this.activity.remoteSetOnResumeAction(this.action);
					this.activity.startActivity(intent);
				} else {
					String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					this.activity.remoteSetOnResumeAction(null);
					new GetCookieTask(this.action).execute(auth_token);
				}
			} catch (OperationCanceledException e) {
				this.activity.remoteSetOnResumeAction(null);
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				this.activity.remoteSetOnResumeAction(null);
				e.printStackTrace();
			} catch (IOException e) {
				this.activity.remoteSetOnResumeAction(null);
				e.printStackTrace();
			}
		}
	};

	private class GetCookieTask extends AsyncTask<String, String, Boolean> {
		RemoteAction action;

		public GetCookieTask(RemoteAction action) {
			this.action = action;
		}

		protected Boolean doInBackground(String... tokens) {
			try {
				// Don't follow redirects
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

				HttpGet http_get = new HttpGet(REMOTE_LOGIN_URL + tokens[0]);
				HttpResponse response;
				response = http_client.execute(http_get);
				boolean result = true;

				if (response.getStatusLine().getStatusCode() != 302) {
					// Response should be a redirect
					result = false;
				} else {
					for (Cookie cookie : http_client.getCookieStore().getCookies()) {
						if (cookie.getName().equals("ACSID")) {
							result = true;
							break;
						}
					}
				}
				response.getEntity().consumeContent();
				return result;

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			new AuthenticatedRequestTask().execute(action);
		}
	}

	private class AuthenticatedRequestTask extends AsyncTask<RemoteAction, Integer, RemoteAction> {

		@Override
		protected RemoteAction doInBackground(RemoteAction... actions) {
			RemoteAction action = actions[0];
			try {
				HttpRequestBase[] requests = action.createRequests(REMOTE_REQUEST_URL);
				for (int i = 0; i < requests.length; i++) {
					action.aboutToRun(i);
					HttpResponse response = http_client.execute(requests[i]); 
					
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
				return action;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				action.error("illegal state");
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				action.error("protocol");
			} catch (IOException e) {
				e.printStackTrace();
				action.error("protocol");
			} catch (NullPointerException e) {
				e.printStackTrace();
				action.error("null");
			}
			return null;
		}
		

		protected void onPostExecute(RemoteAction result) {
			result.close();
		}
	}

}
