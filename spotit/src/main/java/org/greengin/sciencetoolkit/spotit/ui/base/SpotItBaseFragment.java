package org.greengin.sciencetoolkit.spotit.ui.base;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.greengin.sciencetoolkit.common.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.ui.about.AboutActivity;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventFragment;
import org.greengin.sciencetoolkit.spotit.ui.remote.SpotItProjectBrowserActivity;

public class SpotItBaseFragment extends SpotItEventFragment {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        int menuResource = RemoteApi.get().isLogged() ?
                R.menu.projects_logged_in :
                R.menu.projects_logged_out;

        inflater.inflate(menuResource, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_application_connect:
                Intent connectIntent = new Intent(getActivity(),
                        SpotItProjectBrowserActivity.class);
                startActivity(connectIntent);
                return true;
            case R.id.action_application_about:
                Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
