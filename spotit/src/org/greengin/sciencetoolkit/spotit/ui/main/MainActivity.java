package org.greengin.sciencetoolkit.spotit.ui.main;

import org.greengin.sciencetoolkit.common.ui.base.SwipeActivity;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.ui.main.projects.ProjectsFragment;

import android.support.v4.app.Fragment;

public class MainActivity extends SwipeActivity {
	private static int lastTab = -1;
	
	public MainActivity() {
		super(false);
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
		return R.layout.view_main;
	}

	@Override
	public int getViewPagerLayoutId() {
		return R.id.pager;
	}

	@Override
	public int getTabCount() {
		return 1;
	}

	@Override
	public Fragment createTabFragment(int position) {
		switch (position) {
		case 0:
			return new ProjectsFragment();
/*		case 1:
			return new RecordFragment();
		case 2:
			return new ShareFragment();*/
		default:
			return null;
		}
	}

	@Override
	public CharSequence getTabTitle(int position) {
		switch (position) {
		case 0:
			return getString(R.string.main_activity_tab_1);
		case 1:
			return getString(R.string.main_activity_tab_2);
		case 2:
			return getString(R.string.main_activity_tab_3);
		default:
			return null;
		}
	}


}
