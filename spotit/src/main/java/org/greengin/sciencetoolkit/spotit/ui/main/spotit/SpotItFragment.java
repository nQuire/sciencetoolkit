package org.greengin.sciencetoolkit.spotit.ui.main.spotit;

import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.ui.base.SpotItBaseFragment;
import org.greengin.sciencetoolkit.spotit.ui.main.MainActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


public class SpotItFragment extends SpotItBaseFragment implements OnClickListener {
    Button buttonSpotIt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.view_spotit, container,
                false);

        buttonSpotIt = (Button) rootView.findViewById(R.id.spotit_camera);
        buttonSpotIt.setOnClickListener(this);
        return rootView;
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