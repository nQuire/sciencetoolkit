package org.greengin.sciencetoolkit.ui;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CreateProfileDialogFragment extends DialogFragment {

	private static final String SET_AS_ACTIVE = "SET_AS_ACTIVE";
	private static final String COPY_FROM_DEFAULT = "COPY_FROM_DEFAULT";

	public static void showCreateProfileDialog(FragmentManager fm, boolean setAsActive, boolean copyFromDefault) {
		CreateProfileDialogFragment dialog = new CreateProfileDialogFragment();
		Bundle args = new Bundle();
		args.putBoolean(SET_AS_ACTIVE, setAsActive);
		args.putBoolean(COPY_FROM_DEFAULT, copyFromDefault);
		dialog.setArguments(args);
		dialog.show(fm, "create_new_profile");
	}

	Button ok;
	Model profile;
	EditText title;
	boolean setAsActive;
	boolean copyFromDefault;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_create_profile, container);
		getDialog().setTitle(getResources().getString(R.string.create_new_profile));

		this.setAsActive = getArguments().getBoolean(CreateProfileDialogFragment.SET_AS_ACTIVE);
		this.copyFromDefault = getArguments().getBoolean(CreateProfileDialogFragment.COPY_FROM_DEFAULT);

		ok = (Button) view.findViewById(R.id.ok);
		ok.setEnabled(false);

		Button cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}

		});

		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String profileTitle = title.getText().toString();
				if (profileTitle.length() > 0) {
					ProfileManager.i().createProfile(profileTitle, setAsActive, copyFromDefault);
					dismiss();
				}
			}
		});

		title = (EditText) view.findViewById(R.id.new_profile_title);
		title.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				ok.setEnabled(s.length() > 0);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
		
		return view;
	}

}
