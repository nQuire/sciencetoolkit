package org.greengin.sciencetoolkit.spotit.ui.main.images;

import java.util.List;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.logic.data.DataManager;
import org.greengin.sciencetoolkit.spotit.logic.remote.UploadRemoteAction;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;
import org.greengin.sciencetoolkit.spotit.ui.base.SpotItBaseFragment;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventManagerListener;
import org.greengin.sciencetoolkit.spotit.ui.main.MainActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;


public class ImagesFragment extends SpotItBaseFragment implements ImageListener, ImageActionListener {

    ImagesGridAdapter adapter;
    TextView headerText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventManager.setListener(new EventListener());

        eventManager.listenToSettings("profiles");
        eventManager.listenToLoggedData();
        eventManager.listenToProfiles();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.view_images, container,
                false);

        this.adapter = new ImagesGridAdapter(inflater, this);

        GridView list = (GridView) rootView.findViewById(R.id.image_list);
        list.setAdapter(adapter);

        headerText = (TextView) rootView.findViewById(R.id.images_header);

        updateView();
        return rootView;
    }

    private void updateView() {
        Model project = ProjectManager.get().getActiveProject();
        if (project != null) {
            headerText.setText(getResources().getString(R.string.image_header_project, project.getString("title")));
        } else {
            headerText.setText(getText(R.string.image_header_no_project));
        }
    }

    private class EventListener extends SpotItEventManagerListener {
        @Override
        public void events(List<String> settingsEvents, List<String> projectEvents, List<String> dataEvents, boolean whilePaused) {
            updateView();
            adapter.updateData();
        }
    }

    @Override
    public void imageUpload(Model observation) {
        ImageUploadDlg.open(getActivity(), observation, this);
    }

    @Override
    public void imageDelete(Model observation) {
        ImageDeleteDlg.open(getActivity(), observation, this);
    }


    @Override
    public void imageDeleted(Model observation) {
        DataManager.get().deleteData(observation);
    }

    @Override
    public void imageUploaded(Model observation) {
        ((MainActivity) getActivity()).remoteRequest(new UploadRemoteAction(getActivity(), observation));
    }

}
