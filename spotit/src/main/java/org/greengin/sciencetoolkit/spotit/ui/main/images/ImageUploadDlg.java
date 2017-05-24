package org.greengin.sciencetoolkit.spotit.ui.main.images;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;

public class ImageUploadDlg {

    public static void open(Context context, Model observation, ImageActionListener listener) {
        new ImageUploadDlgBuilder(context, observation, listener).launch();
    }

    private static class ImageUploadDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener {

        AlertDialog dlg;
        Model observation;
        ImageActionListener listener;
        EditText titleEdit;

        public ImageUploadDlgBuilder(Context context, Model observation, ImageActionListener listener) {
            super(context);

            this.listener = listener;
            this.observation = observation;

            setTitle(context.getString(R.string.image_upload_dlg_title));

            View view = LayoutInflater.from(context).inflate(R.layout.dlg_upload_image, null);

            TextView text = (TextView) view.findViewById(R.id.image_upload_dlg_msg);
            text.setText(String.format(context.getString(R.string.image_upload_dlg_msg),
                    ProjectManager.get().getActiveProject().getString("title")));

            String title = observation.getString("title");
            titleEdit = (EditText) view.findViewById(R.id.upload_img_title);
            titleEdit.setText(title);

            setPositiveButton(context.getResources().getString(R.string.button_label_upload), this);
            setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);

            setView(view);
        }

        public void launch() {
            dlg = this.show();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String text = titleEdit.getText().toString();
            observation.setString("title", text);

            if (which == DialogInterface.BUTTON_POSITIVE) {
                listener.imageUploaded(observation);
            }
            dlg.dismiss();
        }
    }

}
