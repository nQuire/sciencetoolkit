package org.greengin.sciencetoolkit.ui;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public abstract class ParentListActivity extends SettingsControlledActivity {


	int childrenContainerId;
	ReentrantLock lock;


	public ParentListActivity(int childrenContainerId) {
		super();
		this.childrenContainerId = childrenContainerId;
		this.lock = new ReentrantLock();
	}



	protected void updateChildrenList() {
		lock.lock();
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		List<Fragment> list = getSupportFragmentManager().getFragments();
		if (list != null) {
			for (Fragment fragment : list) {
				ft.remove(fragment);
			}
		}
		
		for (Fragment fragment : getUpdatedFragmentChildren()) {
			ft.add(childrenContainerId, fragment);
		}
		
		ft.commit();
		
		lock.unlock();
	}

	protected abstract List<Fragment> getUpdatedFragmentChildren();
	protected abstract boolean removeChildFragmentOnUpdate(Fragment child);

}
