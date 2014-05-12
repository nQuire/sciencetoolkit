package org.greengin.sciencetoolkit.spotit.ui.base.events;

import org.greengin.sciencetoolkit.common.ui.base.events.EventActivity;

public class SpotItEventActivity extends EventActivity<SpotItEventManager> {

	@Override
	protected SpotItEventManager createEventManager() {
		return new SpotItEventManager();
	}

}
