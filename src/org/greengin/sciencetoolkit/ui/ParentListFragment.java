package org.greengin.sciencetoolkit.ui;

import java.util.List;

import android.support.v4.app.Fragment;

public abstract class ParentListFragment extends Fragment {

	int childrenContainerId;
	List<Fragment> children;

	public ParentListFragment(int childrenContainerId) {
		super();
		this.childrenContainerId = childrenContainerId;
		children = null;
	}

	protected void updateChildrenList() {
		if (children != null) {
			for (Fragment fragment : children) {
				getChildFragmentManager().beginTransaction().remove(fragment).commit();
			}
		}
		
		children = getUpdatedFragmentChildren();
		for (Fragment fragment : children) {
			getChildFragmentManager().beginTransaction().add(childrenContainerId, fragment).commit();
		}
	}

	protected abstract List<Fragment> getUpdatedFragmentChildren();

}
