package org.greengin.sciencetoolkit.ui.components.main.datalogging.edit;

import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.ParentListActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;

public class DataLoggingEditActivity extends ParentListActivity implements ModelNotificationListener, DataLoggerStatusListener {

	public DataLoggingEditActivity() {
		super(R.id.sensor_list);
	}

	String profileId;
	Model profile;

	EditText edit;
	Button add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		profileId = getIntent().getExtras().getString(Arguments.ARG_PROFILE);
		profile = ProfileManager.getInstance().get(profileId);

		setContentView(R.layout.activity_data_logging_edit);

		View rootView = getWindow().getDecorView();
		edit = (EditText) rootView.findViewById(R.id.current_profile_name);
		add = (Button) rootView.findViewById(R.id.add_sensor);

		if (profile != null) {
			edit.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {
					profile.setString("title", s.toString());
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				}

				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				}
			});
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateTitle();
		updateChildrenList();
		updateSettingsEnabled();
		ProfileManager.getInstance().registerDirectListener(this);
		DataLogger.getInstance().registerStatusListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		ProfileManager.getInstance().unregisterDirectListener(this);
		DataLogger.getInstance().unregisterStatusListener(this);
	}

	private void updateTitle() {
		if (profile != null) {
			edit.setText(profile.getString("title"));
		}
	}

	private void updateSettingsEnabled() {
		boolean enabled = profile != null && !DataLogger.getInstance().isRunning();
		add.setEnabled(enabled);
		edit.setEnabled(enabled);
	}

	public void actionAddSensor(View view) {
		FragmentManager fm = getSupportFragmentManager();
		AddSensorDialogFragment dialog = new AddSensorDialogFragment();
		dialog.show(fm, "add_sensor");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_logging_edit, menu);
		return true;
	}


	@Override
	public void modelNotificationReceived(String msg) {
		updateChildrenList();
	}

	@Override
	protected List<Fragment> getUpdatedFragmentChildren() {
		Vector<Fragment> fragments = new Vector<Fragment>();

		Vector<Model> profileSensors = profile.getModel("sensors", true).getModels("weight");
		for (Model profileSensor : profileSensors) {
			ProfileSensorOrganizeFragment fragment = new ProfileSensorOrganizeFragment();
			Bundle args = new Bundle();
			args.putString(Arguments.ARG_PROFILE, profile.getString("id"));
			args.putString(Arguments.ARG_SENSOR, profileSensor.getString("id"));
			fragment.setArguments(args);
			fragments.add(fragment);
		}

		return fragments;
	}
	
	@Override
	protected boolean removeChildFragmentOnUpdate(Fragment child) {
		return child instanceof ProfileSensorOrganizeFragment;
	}


	@Override
	public void dataLoggerStatusModified() {
		updateSettingsEnabled();
	}


}
