package com.scanlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jhansi on 05/04/15.
 */
public class Utils {

    private Utils() {

    }

    public static Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

    public static Uri saveBitmap(Context context, Bitmap bmp, String filename, int compress) throws IOException {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.JPEG, compress, bytes);

        //File f = new File(Environment.getExternalStorageDirectory() + File.separator + "testimage.jpg");
        File f = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);

        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return android.net.Uri.parse(f.toURI().toString());
    }

    //TODO make delete file method
}