package org.greengin.sciencetoolkit.ui;

import java.io.File;

import org.greengin.sciencetoolkit.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class ShareClickListener implements DialogInterface.OnClickListener {
	File share;
	String profileTitle;
	Context context;

	public ShareClickListener(Context context, String profileTitle, File share) {
		this.context = context;
		this.profileTitle = profileTitle;
		this.share = share;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		String shareMenuTitle = context.getResources().getString(R.string.export_menu_title);
		String subject = String.format(context.getResources().getString(R.string.export_payload_subject), profileTitle);
		String body = context.getResources().getString(R.string.export_payload_body);

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.setType("*/*");
		sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {});
		sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(share));
		context.startActivity(Intent.createChooser(sendIntent, shareMenuTitle));
	}
}