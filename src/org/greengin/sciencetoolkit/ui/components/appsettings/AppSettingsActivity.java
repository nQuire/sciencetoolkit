package org.greengin.sciencetoolkit.ui.components.appsettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppSettingsActivity extends SettingsControlledActivity {

	public static final String SELECT_ACCOUNT = "SELECT_ACCOUNT";

	DefaultHttpClient http_client = new DefaultHttpClient();
	protected AccountManager accountManager;
	protected Intent intent;
	Account[] accounts;

	Button selectAccount;
	TextView accountLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);

		SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.app_settings, "app");

		View root = getWindow().getDecorView();
		selectAccount = (Button) root.findViewById(R.id.app_account_select);
		accountLabel = (TextView) root.findViewById(R.id.app_account);

		selectAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getAuthToken();
			}
		});

		accountManager = AccountManager.get(getApplicationContext());
		accounts = accountManager.getAccountsByType("com.google");

		ListView listView = (ListView) root.findViewById(R.id.account_list);
		listView.setAdapter(new ArrayAdapter<Account>(this, R.layout.list_accounts_item, accounts));
	}

	private void getAuthToken() {
		Log.d("stk auth", "get auth token");
		accountManager.getAuthToken(accounts[0], "ah", null, this, new GetAuthTokenCallback(), null);
	}

	protected void onGetAuthToken(Bundle bundle) {
		Log.d("stk auth", "onget auth token!");
		String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
		new GetCookieTask().execute(auth_token);
	}

	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {
		public void run(AccountManagerFuture<Bundle> result) {
			Log.d("stk auth", "run get auth");
			Bundle bundle;
			try {
				bundle = result.getResult();
				Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					Log.d("stk auth", "get auth intent");
					// User input required
					startActivity(intent);
				} else {
					Log.d("stk auth", "get auth no intent");
					onGetAuthToken(bundle);
				}
			} catch (OperationCanceledException e) {
				Log.d("stk auth", "get auth op canceled");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				Log.d("stk auth", "get auth authenticator exception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("stk auth", "get auth ioexception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private class GetCookieTask extends AsyncTask<String, String, Boolean> {
		protected Boolean doInBackground(String... tokens) {
			Log.d("stk auth", "get cookie run");
			try {
				// Don't follow redirects
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

				HttpGet http_get = new HttpGet("http://engaged-shade-411.appspot.com/_ah/login?continue=http://localhost/&auth=" + tokens[0]);
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
			new AuthenticatedRequestTask().execute("https://engaged-shade-411.appspot.com/projects?action=list");
		}
	}

	private class AuthenticatedRequestTask extends AsyncTask<String, String, HttpResponse> {
		@Override
		protected HttpResponse doInBackground(String... urls) {
			Log.d("stk auth", "request task run");
			try {
				HttpGet http_get = new HttpGet(urls[0]);
				return http_client.execute(http_get);
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

		protected void onPostExecute(HttpResponse result) {
			Log.d("stk auth", "request task post execute");
			try {

				BufferedReader reader = new BufferedReader(new InputStreamReader(result.getEntity().getContent()));
				Log.d("stk auth", "request task answer:");
				Log.d("stk auth", "");
				String line;
				while ((line = reader.readLine()) != null) {
					Log.d("stk auth", line);
				}
				reader.close();
				result.getEntity().consumeContent();

				Log.d("stk auth", "");
				Toast.makeText(getApplicationContext(), "good!", Toast.LENGTH_LONG).show();
			} catch (IllegalStateException e) {
				Log.d("stk auth", "request task illegalstateexception");
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("stk auth", "request task ioexception");
				e.printStackTrace();
			} catch (NullPointerException e) {
				Log.d("stk auth", "request task nullpointer");
				e.printStackTrace();
			}
		}
	}
}
