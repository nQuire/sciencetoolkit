package org.greengin.sciencetoolkit.ui.components.main.data.view.visualizations;

import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.components.main.data.view.AbstractDataVisualizationFragment;
import org.greengin.sciencetoolkit.ui.plotting.ProfileDataXYSensorPlotFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlotVisualizationFragment extends AbstractDataVisualizationFragment {

	public PlotVisualizationFragment() {
		super(R.id.plot_list);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_data_view_plot, container, false);
		
		updateChildrenList();
		
		return rootView;
	} 
	
	
	@Override
	protected void updateDataRange() {
		
	}
	
	@Override
	protected List<Fragment> getUpdatedFragmentChildren() {
		Vector<Fragment> fragments = new Vector<Fragment>();
		
		Model profile = ProfileManager.getInstance().get(profileId);
		if (profile != null) {
			for(Model profileSensor : profile.getModel("sensors", true).getModels("weight")) {
				String sensorId = profileSensor.getString("id");
				Fragment f = new ProfileDataXYSensorPlotFragment();
				Bundle args = new Bundle();
				args.putString(Arguments.ARG_PROFILE, profileId);
				args.putString(Arguments.ARG_SENSOR, sensorId);
				f.setArguments(args);
				fragments.add(f);
			}
		}
		
		return fragments;
	}

	@Override
	protected boolean removeChildFragmentOnUpdate(Fragment child) {
		return child instanceof ProfileDataXYSensorPlotFragment;
	}

}
