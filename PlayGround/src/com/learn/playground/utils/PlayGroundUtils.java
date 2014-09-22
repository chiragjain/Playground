
package com.learn.playground.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class PlayGroundUtils {
    
    public static final int MEDIA_TYPE_IMAGE = 0;
    public static final int MEDIA_TYPE_IMAGE_CROP = 0;
    public static final int MEDIA_TYPE_VIDEO = 1;
    public static final int SAVE_VIDEO = 2;

    public static Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            return null;
        }

        return bitmap;
    }

    public static String getImagePath(Uri uri, Context context) {
        String selectedImagePath;
        // 1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
        String[] projection = {
                MediaStore.Images.Media.DATA
        };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            selectedImagePath = cursor.getString(column_index);
        } else {
            selectedImagePath = null;
        }

        if (selectedImagePath == null) {
            // 2:OI FILE Manager --- call method: uri.getPath()
            selectedImagePath = uri.getPath();
        }
        return selectedImagePath;
    }

    public static String getVideoPath(Uri uri, Context context) {
        String selectedImagePath;

        String[] projection = {
                MediaStore.Video.Media.DATA
        };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            selectedImagePath = cursor.getString(column_index);
        } else {
            selectedImagePath = null;
        }

        if (selectedImagePath == null) {
            // 2:OI FILE Manager --- call method: uri.getPath()
            selectedImagePath = uri.getPath();
        }
        return selectedImagePath;
    }

    public static Bitmap decodeBitmapFromStream(Context context,
            Uri selectedImage, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options;

        Object localObject = null;
        try {
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = true;
            options.inScaled = false;
            options.inDither = false;
            options.inSampleSize = 1;
            BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(selectedImage), null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
            options.inJustDecodeBounds = false;
            localObject = BitmapFactory.decodeStream(context
                    .getContentResolver().openInputStream(selectedImage), null,
                    options);
        } catch (Exception e) {
            Log.d("Image Compress Error", e.getMessage());
        }

        ExifInterface ei;
        int orientation = 0;
        try {
            ei = new ExifInterface(getImagePath(selectedImage, context));
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    localObject = rotateImage((Bitmap) localObject, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    localObject = rotateImage((Bitmap) localObject, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    localObject = rotateImage((Bitmap) localObject, 270);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return (Bitmap) localObject;

    }

    public static Bitmap rotateImage(Bitmap bitmap, int angle) {

        Matrix matrix = new Matrix();
        matrix.setRotate(angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        }

        return inSampleSize;
    }

    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!hasStorage(true)) {
            return null;
        }
        File mediaStorageDir = new File(Environment
                .getExternalStorageDirectory().toString() + "/Scanner/");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile = null;

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir, "image" + ".jpg");
        } else if (type == MEDIA_TYPE_IMAGE_CROP) {
            mediaFile = new File(mediaStorageDir, "image_crop" + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir, "video" + ".mp4");
        } else if (type == SAVE_VIDEO) {
            mediaFile = new File(mediaStorageDir, System.currentTimeMillis()
                    + "video" + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    
    public static boolean hasStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (requireWriteAccess) {
                boolean writable = checkFsWritable();
                return writable;
            } else {
                return true;
            }
        } else if (!requireWriteAccess
                && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    private static boolean checkFsWritable() {
        String directoryName = Environment.getExternalStorageDirectory()
                .toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        return directory.canWrite();
    }


}
