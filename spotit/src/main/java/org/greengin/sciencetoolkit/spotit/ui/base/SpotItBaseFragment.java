package org.greengin.sciencetoolkit.spotit.ui.base;

import android.view.Menu;
import android.view.MenuInflater;

import org.greengin.sciencetoolkit.common.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventFragment;

public class SpotItBaseFragment extends SpotItEventFragment {

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		int menuResource = RemoteApi.get().isLogged() ?
				R.menu.projects_logged_in:
				R.menu.projects_logged_out;

		inflater.inflate(menuResource, menu);
	}

}
