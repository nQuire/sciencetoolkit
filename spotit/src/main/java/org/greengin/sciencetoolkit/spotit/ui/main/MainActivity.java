package org.greengin.sciencetoolkit.spotit.ui.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.ui.base.SwipeActivity;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.logic.data.DataManager;
import org.greengin.sciencetoolkit.spotit.logic.location.CurrentLocation;
import org.greengin.sciencetoolkit.spotit.ui.main.images.ImagesFragment;
import org.greengin.sciencetoolkit.spotit.ui.main.projects.ProjectsFragment;
import org.greengin.sciencetoolkit.spotit.ui.main.spotit.SpotItFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

public class MainActivity extends SwipeActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static int lastTab = -1;
    private static String newFile = null;
    CurrentLocation currentLocation;

    public MainActivity() {
        super(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLocation = new CurrentLocation(this);
    }

    @Override
    public int getOnResumeTab() {
        return lastTab;
    }

    @Override
    public void setOnResumeTab(int position) {
        lastTab = position;
    }

    @Override
    public int getContentViewLayoutId() {
        return R.layout.view_main;
    }

    @Override
    public int getViewPagerLayoutId() {
        return R.id.pager;
    }

    @Override
    public int getTabCount() {
        return 3;
    }

    @Override
    public Fragment createTabFragment(int position) {
        switch (position) {
            case 0:
                return new SpotItFragment();
            case 1:
                return new ImagesFragment();
            case 2:
                return new ProjectsFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getTabTitle(int position) {
        switch (position) {
            case 0:
                return getString(R.string.main_activity_tab_1);
            case 1:
                return getString(R.string.main_activity_tab_2);
            case 2:
                return getString(R.string.main_activity_tab_3);
            default:
                return null;
        }
    }

    public void captureImage() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = getImageFile();
        Uri uri = Uri.fromFile(imageFile);
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        newFile = imageFile.getAbsolutePath();
        startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK && newFile != null) {
            String uri = newFile;
            newFile = null;
            Model newImage = DataManager.get().newData(uri);
            currentLocation.request(newImage);
            setTab(1);
        }
    }

    private File getImageFile() {
        File basePath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File mediaStorageDir = new File(basePath, "spot_it");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("spotit", "failed to create directory");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK)
                .format(new Date());
        File outputFile = new File(mediaStorageDir, String.format("IMG_%s.jpg",
                timeStamp));

        for (int i = 1; outputFile.exists(); i++) {
            outputFile = new File(mediaStorageDir, String.format(
                    "IMG_%s_d.jpg", timeStamp, i));
        }

        return outputFile;
    }

}
