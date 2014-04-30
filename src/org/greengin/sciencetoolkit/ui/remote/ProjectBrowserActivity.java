package org.greengin.sciencetoolkit.ui.remote;

import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.logic.remote.RemoteCapableActivity;

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


public class ProjectBrowserActivity extends RemoteCapableActivity implements ProjectMembershipListener {

	ProjectBrowserListAdapter adapter;
	BroadcastReceiver eventReceiver;


	TextView status;
	Button loginToggle;
	ListView list;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_project_browser);
		
		eventReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				updateView();				
			}
			
		};
		
		adapter = new ProjectBrowserListAdapter(this, LayoutInflater.from(this));
		
		status = (TextView) findViewById(R.id.login_status);
		loginToggle = (Button) findViewById(R.id.login_toggle);
		list = (ListView) findViewById(R.id.project_list);
		
		
		list.setAdapter(adapter);

		Vector<ProjectData> projects = new Vector<ProjectData>();

		for (int i = 1; i < 5; i++) {
			ProjectData p = new ProjectData();
			p.id = i;
			p.title = "p" + i;
			p.author = "a" + i;
			p.joined = i % 2 == 0;
			projects.add(p);
		}

		adapter.updateProjectList(projects);
		
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
		
		LocalBroadcastManager.getInstance(this).registerReceiver(eventReceiver, new IntentFilter(RemoteApi.REMOTE_EVENT_FILTER));
		updateView();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(eventReceiver);
	}

	@Override
	public void projectMembershipAction(long projectId, boolean join) {
	}
	
	private void updateView() {
		if (RemoteApi.get().isLogged()) { 
			loginToggle.setText(R.string.project_browser_logout);
			status.setText(String.format(getResources().getString(R.string.project_browser_logged_in), RemoteApi.get().getUsername()));
			list.setVisibility(View.VISIBLE);			
		} else {
			loginToggle.setText(R.string.project_browser_login);
			status.setText(R.string.project_browser_not_logged);
			list.setVisibility(View.GONE);			
		}
		
		findViewById(R.id.project_browser_header).invalidate();
		Log.d("stk projects", "updated");
	}

}
