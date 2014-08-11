package org.greengin.sciencetoolkit.ui.dataviewer;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.common.ui.base.AppSettingsActivity;
import org.greengin.sciencetoolkit.common.ui.base.SwipeActivity;
import org.greengin.sciencetoolkit.ui.base.SenseItArguments;
import org.greengin.sciencetoolkit.ui.base.plot.series.SeriesXYSensorPlotFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DataViewerActivity extends SwipeActivity {
	

	private static int lastTab = -1;

	String profileId;

	public DataViewerActivity() {
		super(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		profileId = getIntent().getStringExtra(SenseItArguments.ARG_PROFILE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.data_viewer, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.data_viewer_close) {
			finish();
			return true;
		} 
		
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public int getOnResumeTab() {
		return lastTab;
	}

	@Override
	public void setOnResumeTab(int position) {
		lastTab = position;
	}

	@Override
	public int getContentViewLayoutId() {
		return R.layout.view_data;
	}

	@Override
	public int getViewPagerLayoutId() {
		return R.id.pager;
	}

	@Override
	public int getTabCount() {
		return 2;
	}

	@Override
	public Fragment createTabFragment(int position) {
		Bundle arguments = new Bundle();
		arguments.putString(SenseItArguments.ARG_PROFILE, profileId);
		Fragment f = position == 0 ? new SeriesListFragment() : new SeriesXYSensorPlotFragment();
		f.setArguments(arguments);
		return f;
	}

	@Override
	public CharSequence getTabTitle(int position) {
		switch (position) {
		case 0:
			return getString(R.string.data_viewer_tab_1);
		case 1:
			return getString(R.string.data_viewer_tab_2);
		default:
			return null;
		}
	}

}
