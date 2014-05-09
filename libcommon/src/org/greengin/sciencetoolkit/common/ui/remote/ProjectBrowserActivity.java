package org.greengin.sciencetoolkit.common.ui.remote;

import java.util.Vector;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.greengin.sciencetoolkit.common.R;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteJsonAction;
import org.greengin.sciencetoolkit.common.ui.base.RemoteCapableActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.content.BroadcastReceiver;

public abstract class ProjectBrowserActivity extends RemoteCapableActivity implements ProjectMembershipListener {

	public static final String REMOTE_PROJECT_DATA_EVENT_FILTER = "REMOTE_PROJECT_DATA_EVENT_FILTER";
	
	String projectType;
	
	ProjectBrowserListAdapter adapter;
	BroadcastReceiver loginEventReceiver;
	BroadcastReceiver projectsEventReceiver;

	TextView status;
	Button loginToggle;
	ListView list;
	
	public ProjectBrowserActivity(String projectType) {
		super();
		this.projectType = projectType;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_project_browser);

		loginEventReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateView();
				if (RemoteApi.get().isLogged()) {
					remoteRequest(new ProjectMembershipAction());
				}
			}
		};
		projectsEventReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				adapter.notifyDataSetChanged();
			}
		};


		adapter = new ProjectBrowserListAdapter(this, LayoutInflater.from(this));

		status = (TextView) findViewById(R.id.login_status);
		loginToggle = (Button) findViewById(R.id.login_toggle);
		list = (ListView) findViewById(R.id.project_list);

		list.setAdapter(adapter);

		loginToggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (RemoteApi.get().isLogged()) {
					RemoteApi.get().logout();
				} else {
					RemoteApi.get().tryToLogin(ProjectBrowserActivity.this);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_browser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(this).registerReceiver(loginEventReceiver, new IntentFilter(RemoteApi.REMOTE_LOGIN_EVENT_FILTER));
		LocalBroadcastManager.getInstance(this).registerReceiver(projectsEventReceiver, new IntentFilter(REMOTE_PROJECT_DATA_EVENT_FILTER));
		updateView();

		if (RemoteApi.get().isLogged()) {
			remoteRequest(new ProjectMembershipAction());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(loginEventReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(projectsEventReceiver);
	}

	@Override
	public void projectMembershipAction(long projectId, boolean join) {
		remoteRequest(new ProjectMembershipAction(join ? "join" : "leave", projectId));
	}

	private void updateView() {
		if (RemoteApi.get().isLogged()) {
			loginToggle.setText(R.string.project_browser_logout);
			status.setText(String.format(getResources().getString(R.string.project_browser_logged_in), RemoteApi.get().getUsername()));
			list.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
		} else {
			loginToggle.setText(R.string.project_browser_login);
			status.setText(R.string.project_browser_not_logged);
			list.setVisibility(View.GONE);
		}

		findViewById(R.id.project_browser_header).invalidate();
		Log.d("stk projects", "updated");
	}
	

	/**
	 * 
	 * Example of server output:
	 * 
	 * [ {"id":11,"title":"Fastest lift in the UK","author":"author","joined":
	 * false}, {"id":32768,"title":"lifts!","author":"evilfer","joined":false} ]
	 * 
	 */

	private class ProjectMembershipAction extends RemoteJsonAction {

		String action;
		long projectId;

		public ProjectMembershipAction() {
			this(null, 0);
		}

		public ProjectMembershipAction(String action, long projectId) {
			this.projectId = projectId;
			this.action = action;
		}

		@Override
		public HttpRequestBase[] createRequests(String urlBase) {
			HttpRequestBase[] requests = new HttpRequestBase[1];

			if (action == null) {
				requests[0] = new HttpGet(String.format("%sprojects/%s", urlBase, projectType));
			} else {
				requests[0] = new HttpPut(String.format("%sprojects/%s/%d/%s", urlBase, projectType, projectId, action));
			}

			return requests;
		}

		@Override
		public void result(int request, JSONObject shouldbenull, JSONArray result) {
			Log.d("stk remote", "projects: " + result.length());
			
			Vector<ProjectData> projects = new Vector<ProjectData>();

			try {
				for (int i = 0; i < result.length(); i++) {
					JSONObject obj = result.getJSONObject(i);
					ProjectData p = new ProjectData();
					p.id = obj.getLong("id");
					p.title = obj.getString("title");
					p.joined = obj.getBoolean("joined");
					p.author = obj.getString("author");

					projects.add(p);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			adapter.updateProjectList(projects);
			Intent i = new Intent(REMOTE_PROJECT_DATA_EVENT_FILTER);
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);

			if (action != null) {
				projectMembershipUpdated();
			}
		}
	}
	
	protected abstract void projectMembershipUpdated();

}
