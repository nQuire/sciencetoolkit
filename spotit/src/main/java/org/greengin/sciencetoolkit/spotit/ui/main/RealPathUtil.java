package org.greengin.sciencetoolkit.spotit.ui.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class RealPathUtil {

    public static String getRealPath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        } else if (android.os.Build.VERSION.SDK_INT >= 19) {
            return getRealPathFromURI_API19(context, uri);
        } else if (android.os.Build.VERSION.SDK_INT >= 11) {
            return getRealPathFromURI_API11to18(context, uri);
        } else {
            return getRealPathFromURI_BelowAPI11(context, uri);
        }
    }

    @TargetApi(19)
    private static String getRealPathFromURI_API19(Context context, Uri uri) {

        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        String filePath = cursor.moveToFirst() ? cursor.getString(columnIndex) : null;
        cursor.close();
        return filePath;
    }


    @TargetApi(11)
    private static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    private static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}