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
import org.greengin.sciencetoolkit.ui.remote.RemoteAction;
import org.greengin.sciencetoolkit.ui.remote.RemoteCapableActivity;

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
import android.util.Log;

public class RemoteApi implements ModelNotificationListener {
	static final String REMOTE_LOGIN_URL = "https://nquireprojects.appspot.com/_ah/login?auth=";
	static final String REMOTE_REQUEST_URL = "https://nquireprojects.appspot.com/api/subscriptions";

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
			Log.d("stk remote", "account: " + this.account);
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
			Log.d("stk auth", "get auth token");
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
			Log.d("stk auth", "run get auth");
			Bundle bundle;
			try {
				bundle = result.getResult();
				Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					Log.d("stk auth", "get auth intent");
					// User input required
					this.activity.remoteSetOnResumeAction(this.action);
					this.activity.startActivity(intent);
				} else {
					Log.d("stk auth", "get auth no intent");
					Log.d("stk auth", "onget auth token!");
					String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					this.activity.remoteSetOnResumeAction(null);
					new GetCookieTask(this.action).execute(auth_token);
				}
			} catch (OperationCanceledException e) {
				this.activity.remoteSetOnResumeAction(null);
				Log.d("stk auth", "get auth op canceled");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				this.activity.remoteSetOnResumeAction(null);
				Log.d("stk auth", "get auth authenticator exception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				this.activity.remoteSetOnResumeAction(null);
				Log.d("stk auth", "get auth ioexception");
				// TODO Auto-generated catch block
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
			Log.d("stk auth", "get cookie run");
			try {
				// Don't follow redirects
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

				HttpGet http_get = new HttpGet(REMOTE_LOGIN_URL + tokens[0]);
				HttpResponse response;
				response = http_client.execute(http_get);
				boolean result = true;

				if (response.getStatusLine().getStatusCode() != 302) {
					Log.d("stk auth", "get cookie response should be a redirect");
					// Response should be a redirect
					result = false;
				} else {
					for (Cookie cookie : http_client.getCookieStore().getCookies()) {
						if (cookie.getName().equals("ACSID")) {
							Log.d("stk auth", "get cookie acsid found");
							result = true;
							break;
						}
					}
				}
				response.getEntity().consumeContent();
				return result;

			} catch (ClientProtocolException e) {
				Log.d("stk auth", "get cookie clientprotocolexception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("stk auth", "get cookie ioexception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			Log.d("stk auth", "get cookie post");
			new AuthenticatedRequestTask().execute(action);
		}
	}

	private class AuthenticatedRequestTask extends AsyncTask<RemoteAction, Integer, RemoteAction> {

		@Override
		protected RemoteAction doInBackground(RemoteAction... actions) {
			RemoteAction action = actions[0];
			Log.d("stk auth", "request task run");
			try {
				HttpRequestBase[] requests = action.createRequests(REMOTE_REQUEST_URL);
				for (int i = 0; i < requests.length; i++) {
					HttpResponse response = http_client.execute(requests[i]); 
					Log.d("stk auth", "request task answer received: " + i);
					String answer = read(response);
					action.result(i, answer);
				}
				return action;
			} catch (ClientProtocolException e) {
				Log.d("stk auth", "request task clientprotocolexception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("stk auth", "request task ioexception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		private String read(HttpResponse response) {			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				Log.d("stk auth", "request task answer:");
				Log.d("stk auth", "");
				String line;
				StringBuffer answer = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					Log.d("stk auth", line);
					answer.append(line).append('\n');
				}
				reader.close();
				response.getEntity().consumeContent();
				
				Log.d("stk auth", "");
				return answer.toString();
				
			} catch (IllegalStateException e) {
				Log.d("stk auth", "request task illegalstateexception");
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				Log.d("stk auth", "request task ioexception");
				e.printStackTrace();
				return null;
			} catch (NullPointerException e) {
				Log.d("stk auth", "request task nullpointer");
				e.printStackTrace();
				return null;
			}
		}


		protected void onPostExecute(RemoteAction result) {
			Log.d("stk auth", "request task post execute");
			result.close();
		}
	}

}
