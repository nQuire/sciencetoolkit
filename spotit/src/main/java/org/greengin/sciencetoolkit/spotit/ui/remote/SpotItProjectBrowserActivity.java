package org.greengin.sciencetoolkit.spotit.ui.remote;

import org.greengin.sciencetoolkit.common.ui.remote.ProjectBrowserActivity;
import org.greengin.sciencetoolkit.spotit.logic.remote.UpdateRemoteAction;

public class SpotItProjectBrowserActivity extends ProjectBrowserActivity {

	public SpotItProjectBrowserActivity() {
		super("spotit");
	}

	@Override
	protected void projectMembershipUpdated() {
		remoteRequest(new UpdateRemoteAction());
	}
}
