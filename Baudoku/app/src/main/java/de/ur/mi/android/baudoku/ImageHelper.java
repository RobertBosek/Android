package de.ur.mi.android.baudoku;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageHelper {

    private static final String DEFAULT_IMAGE_PATH = "/storage/emulated/0/Android/data/de.ur.mi.android.baudoku/files/Pictures/20170918_182042814699927.jgp";


    public static void setPic(String imgPath, ImageView imgView, int size) {
        if (imgPath.equals("")) {
            imgPath = DEFAULT_IMAGE_PATH;
        }
        imgView.getLayoutParams().width = size;
        imgView.getLayoutParams().height = size;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/size, photoH/size);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
        imgView.setImageBitmap(bitmap);
    }
}
