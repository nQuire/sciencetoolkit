package org.greengin.sciencetoolkit.spotit.ui.base.events;

import org.greengin.sciencetoolkit.common.ui.base.events.EventFragment;

public class SpotItEventFragment extends EventFragment<SpotItEventManager> {

	@Override
	protected SpotItEventManager createEventManager() {
		return new SpotItEventManager();
	}

}
