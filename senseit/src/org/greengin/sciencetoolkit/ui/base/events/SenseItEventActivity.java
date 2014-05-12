package org.greengin.sciencetoolkit.ui.base.events;

import org.greengin.sciencetoolkit.common.ui.base.events.EventActivity;

public class SenseItEventActivity extends EventActivity<SenseItEventManager> {

	@Override
	protected SenseItEventManager createEventManager() {
		return new SenseItEventManager();
	}

}
