package org.greengin.sciencetoolkit.ui.base.plot;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.base.dlgs.sensorinfo.SensorInfoDlg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public abstract class ClosableXYSensorPlotFragment extends AbstractXYSensorPlotFragment implements OnClickListener {


	protected ImageButton closeButton;
	protected ImageButton helpButton;

	
	protected abstract String getSensorId();
	
	@Override
	public void onDetach() {
		this.close();
		super.onDetach();
	}
	
	protected int layoutId() {
		return R.layout.panel_plot_closable;
	}

	protected void setHeaderTitle(String title) {
		((TextView) this.getView().findViewById(R.id.plot_header_label)).setText(title);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View plotPanel = super.onCreateView(inflater, container, savedInstanceState);
		closeButton = (ImageButton) plotPanel.findViewById(R.id.plot_close);
		closeButton.setOnClickListener(this);
		helpButton = (ImageButton) plotPanel.findViewById(R.id.sensor_help);
		helpButton.setOnClickListener(this);
		this.close();
		return plotPanel;
	}

	protected void close() {
		destroyPlot();
		this.plotPanel.setVisibility(View.GONE);
	}

	public void openPlot() {
		this.plotPanel.setVisibility(View.VISIBLE);
		this.createPlot();
	}

	@Override
	public void onClick(View v) {
		if (v == closeButton) {
			close();
		} else if (v == helpButton) {
			String sensorId = getSensorId();
			SensorWrapper sensor = SensorWrapperManager.get().getSensor(sensorId);
			if (sensor != null) {
				SensorInfoDlg.open(getActivity(), sensor);				
			}
		}
	}
}
