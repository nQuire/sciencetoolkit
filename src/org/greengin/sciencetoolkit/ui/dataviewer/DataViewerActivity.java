package org.greengin.sciencetoolkit.ui.dataviewer;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.base.Arguments;
import org.greengin.sciencetoolkit.ui.base.SwipeActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class DataViewerActivity extends SwipeActivity {
	private static int lastTab = -1;

	String profileId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		profileId = getIntent().getStringExtra(Arguments.ARG_PROFILE);
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
		arguments.putString(Arguments.ARG_PROFILE, profileId);
		Fragment f = position == 0 ? new SeriesListFragment() : new Fragment();
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
