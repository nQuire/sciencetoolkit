package org.greengin.sciencetoolkit.ui.remote;

import org.greengin.sciencetoolkit.common.ui.remote.ProjectBrowserActivity;
import org.greengin.sciencetoolkit.logic.remote.UpdateRemoteAction;

public class SenseItProjectBrowserActivity extends ProjectBrowserActivity {

	public SenseItProjectBrowserActivity() {
		super("senseit");
	}

	@Override
	protected void projectMembershipUpdated() {
		remoteRequest(new UpdateRemoteAction());
	}

}
