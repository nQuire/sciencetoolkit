package org.greengin.sciencetoolkit.ui;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

public abstract class ParentListActivity extends ActionBarActivity {

	int childrenContainerId;
	List<Fragment> children;

	public ParentListActivity(int childrenContainerId) {
		super();
		this.childrenContainerId = childrenContainerId;
		children = null;
	}

	protected void updateChildrenList() {
		if (children != null) {
			for (Fragment fragment : children) {
				getSupportFragmentManager().beginTransaction().remove(fragment).commit();
			}
		}
		
		children = getUpdatedFragmentChildren();
		for (Fragment fragment : children) {
			getSupportFragmentManager().beginTransaction().add(childrenContainerId, fragment).commit();
		}
	}

	protected abstract List<Fragment> getUpdatedFragmentChildren();

}
