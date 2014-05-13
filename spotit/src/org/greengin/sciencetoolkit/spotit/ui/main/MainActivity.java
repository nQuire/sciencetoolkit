package org.greengin.sciencetoolkit.spotit.ui.main;

import org.greengin.sciencetoolkit.common.ui.base.SwipeActivity;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.logic.data.DataManager;
import org.greengin.sciencetoolkit.spotit.ui.main.projects.ProjectsFragment;
import org.greengin.sciencetoolkit.spotit.ui.main.spotit.SpotItFragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public class MainActivity extends SwipeActivity {
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
	
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
		return 3;
	}

	@Override
	public Fragment createTabFragment(int position) {
		switch (position) {
		case 0:
			return new ProjectsFragment();
		case 1:
			return new Fragment();
		case 2:
			return new SpotItFragment();
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
	
	
	public void captureImage() {
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
	    	DataManager.get().newData(data.getData().toString());
	    }
	}
	
	
	
	


}
