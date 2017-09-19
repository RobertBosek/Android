package de.ur.mi.android.baudoku;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ImageHelper {

    private static final String DEFAULT_IMAGE_PATH = "/storage/emulated/0/Android/data/de.ur.mi.android.baudoku/files/Pictures/20170918_182042814699927.jgp";


    public static void setPic(final String imgPath, final ImageView imgView) {
        ViewTreeObserver vto = imgView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imgView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width  = imgView.getMeasuredWidth();

                boolean useDefault = false;
                if (imgPath.equals("")) {
                    useDefault = true;
                }

                imgView.getLayoutParams().width = width;
                imgView.getLayoutParams().height = width;

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();

                bmOptions.inJustDecodeBounds = true;

                if(useDefault) {
                    BitmapFactory.decodeFile(DEFAULT_IMAGE_PATH, bmOptions);
                } else {
                    BitmapFactory.decodeFile(imgPath, bmOptions);
                }
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                int scaleFactor = Math.min(photoW/imgView.getLayoutParams().width, photoH/imgView.getLayoutParams().height);

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap;
                if(useDefault) {
                    bitmap = BitmapFactory.decodeFile(DEFAULT_IMAGE_PATH, bmOptions);
                } else {
                    bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
                }

                imgView.setImageBitmap(bitmap);
            }
        });
    }
}
