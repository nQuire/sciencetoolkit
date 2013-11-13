package org.greengin.sciencetoolkit.ui;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public abstract class ParentListFragment extends Fragment {

	int childrenContainerId;
	ReentrantLock lock;

	public ParentListFragment(int childrenContainerId) {
		super();
		this.childrenContainerId = childrenContainerId;
		this.lock = new ReentrantLock();
	}

	private void removeChildren(FragmentTransaction ft) {
		List<Fragment> existingfragments = getChildFragmentManager().getFragments();
		if (existingfragments != null) {
			for (Fragment child : existingfragments) {
				if (removeChildFragmentOnUpdate(child)) {
					ft.remove(child);
				}
			}
		}
	}
	protected void clearChildrenList() {
		lock.lock();
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		removeChildren(ft);
		ft.commit();
		lock.unlock();		
	}

	protected void updateChildrenList() {
		lock.lock();

		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		
		removeChildren(ft);

		List<Fragment> newfragments = getUpdatedFragmentChildren();
		if (newfragments != null) {
			for (Fragment fragment : newfragments) {
				ft.add(childrenContainerId, fragment);
			}
		}

		ft.commit();

		lock.unlock();
	}

	protected abstract List<Fragment> getUpdatedFragmentChildren();

	protected abstract boolean removeChildFragmentOnUpdate(Fragment child);

}
