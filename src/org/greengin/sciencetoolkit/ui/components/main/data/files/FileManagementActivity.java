package org.greengin.sciencetoolkit.ui.components.main.data.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.CsvManager;
import org.greengin.sciencetoolkit.ui.ControlledRotationActivity;
import org.greengin.sciencetoolkit.ui.components.appsettings.AppSettingsActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

public class FileManagementActivity extends ControlledRotationActivity implements OnCheckedChangeListener {

	Button[] buttons;
	Vector<String> selected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_file_management);

		View root = getWindow().getDecorView();
		buttons = new Button[] { (Button) root.findViewById(R.id.share_files), (Button) root.findViewById(R.id.delete_files) };
		selected = new Vector<String>();

		setupActionBar();

		updateList();
	}

	private void updateList() {
		View root = getWindow().getDecorView();
		LinearLayout ll = (LinearLayout) root.findViewById(R.id.files_list);

		ll.removeAllViews();

		String[] files = CsvManager.fileList();

		for (String file : files) {
			CheckBox cb = new CheckBox(this);
			cb.setTag(file);
			cb.setText(file);
			cb.setChecked(false);
			cb.setOnCheckedChangeListener(this);

			ll.addView(cb);
		}

		updateButtons();
	}

	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void updateButtons() {
		boolean enabled = selected.size() > 0;
		for (Button b : buttons) {
			b.setEnabled(enabled);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_management, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_application_settings: {
			Intent intent = new Intent(getApplicationContext(), AppSettingsActivity.class);
			startActivity(intent);
		}

		}
		return super.onOptionsItemSelected(item);
	}

	public void onClickShareButton(View view) {
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (String filename : selected) {
			File file = CsvManager.getFile(filename);
			if (file != null) {
				uris.add(Uri.fromFile(file));
			}
		}

		if (uris.size() > 0) {
			String shareMenuTitle = getResources().getString(R.string.export_menu_title);
			String subject = selected.size() == 1 ? getResources().getString(R.string.export_payload_count_s) : String.format(getResources().getString(R.string.export_payload_count_p), selected.size());
			String body = getResources().getString(R.string.export_payload_body);

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
			sendIntent.setType("plain/text");
			sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {});
			sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);

			sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			startActivity(Intent.createChooser(sendIntent, shareMenuTitle));
		}
	}

	public void onClickDeleteButton(View view) {
		new AlertDialog.Builder(view.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.delete_csv_dlg_title).setMessage(R.string.delete_csv_dlg_msg).setPositiveButton(R.string.delete_dlg_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CsvManager.deleteFiles(selected);
				selected.clear();
				updateList();
			}
		}).setNegativeButton(R.string.cancel, null).show();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		String file = (String) buttonView.getTag();

		if (!isChecked) {
			selected.remove(file);
		} else if (!selected.contains(file)) {
			selected.add(file);
		}

		updateButtons();
	}
}
