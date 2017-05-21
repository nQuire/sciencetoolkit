package org.greengin.sciencetoolkit.spotit.ui.main.spotit;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;
import org.greengin.sciencetoolkit.spotit.ui.base.SpotItBaseFragment;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventManagerListener;
import org.greengin.sciencetoolkit.spotit.ui.main.MainActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class SpotItFragment extends SpotItBaseFragment implements OnClickListener {


    Button buttonSpotIt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventManager.setListener(new EventListener());

        eventManager.listenToSettings("profiles");
        eventManager.listenToProfiles();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.view_spotit, container,
                false);

        buttonSpotIt = (Button) rootView.findViewById(R.id.spotit_camera);
        buttonSpotIt.setOnClickListener(this);
        updateView();
        return rootView;
    }

    private void updateView() {
        Model project = ProjectManager.get().getActiveProject();
        if (project != null) {
            buttonSpotIt.setEnabled(true);
            buttonSpotIt.setText(getResources().getString(R.string.capture_image_project, project.getString("title")));
        } else {
            buttonSpotIt.setEnabled(false);
            buttonSpotIt.setText(getText(R.string.capture_no_project));
        }
    }


    private class EventListener extends SpotItEventManagerListener {
        @Override
        public void events(List<String> settingsEvents, List<String> projectEvents, List<String> dataEvents, boolean whilePaused) {
            updateView();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == buttonSpotIt) {
            captureImage();
        }
    }

    private void captureImage() {
        ((MainActivity) getActivity()).captureImage();
    }
}