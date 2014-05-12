package org.greengin.sciencetoolkit.ui.base.events;

import org.greengin.sciencetoolkit.common.ui.base.events.EventFragment;

public class SenseItEventFragment extends EventFragment<SenseItEventManager> {

	@Override
	protected SenseItEventManager createEventManager() {
		return new SenseItEventManager();
	}

}
