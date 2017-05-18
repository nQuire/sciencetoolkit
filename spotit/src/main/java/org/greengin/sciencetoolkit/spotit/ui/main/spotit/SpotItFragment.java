package org.greengin.sciencetoolkit.spotit.ui.main.spotit;

import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.ui.base.SpotItBaseFragment;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventManagerListener;
import org.greengin.sciencetoolkit.spotit.ui.main.MainActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class SpotItFragment extends SpotItBaseFragment implements OnClickListener {
	
	

	ImageButton buttonSpotIt;

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

		View rootView = inflater.inflate(R.layout.view_spotit, container,
				false);

		buttonSpotIt = (ImageButton) rootView.findViewById(R.id.spotit_camera);
		buttonSpotIt.setOnClickListener(this);

		return rootView;
	}


	private class EventListener extends SpotItEventManagerListener {

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