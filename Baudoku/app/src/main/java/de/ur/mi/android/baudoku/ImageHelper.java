package de.ur.mi.android.baudoku;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ImageHelper {

    private static final String DEFAULT_IMAGE_PATH = "/storage/emulated/0/Android/data/de.ur.mi.android.baudoku/files/Pictures/20170918_182042814699927.jgp";

    public static void setPic(final String imgPath, final ImageView imgView) {
        if (!imgPath.equals("")) {
            if (imgView.getMeasuredWidth() == 0) {
                imgView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        imgView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        setImageWithWidth(imgPath, imgView);
                    }
                });
            } else {
                setImageWithWidth(imgPath, imgView);
            }
        }
    }

    private static void setImageWithWidth(String imgPath, ImageView imgView) {
            if (imgPath.equals("")) {
                imgView.setImageBitmap(getBitmap(DEFAULT_IMAGE_PATH, imgView));
            } else{
                imgView.setImageBitmap(getBitmap(imgPath, imgView));
            }
    }

    private static Bitmap getBitmap(String imgPath, ImageView imgView) {
        int width = imgView.getMeasuredWidth();

        imgView.getLayoutParams().width = width;
        imgView.getLayoutParams().height = width;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), getRotationMatrix(imgPath), true);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        double factor;

        if(w >= h){
            factor = (double) width / h;
        } else {
            factor = (double) width / w;
        }

        int wf = (int) (w * factor);
        int hf = (int) (h * factor);

        bitmap = Bitmap.createScaledBitmap(bitmap, wf, hf, true);

        int dx = (width - wf) / (-2);
        int dy = (width - hf) / (-2);

        return Bitmap.createBitmap(bitmap, dx, dy, width, width);
    }


    public static Matrix getRotationMatrix(String imgPath) {
        Matrix matrix = new Matrix();
        try {
            ExifInterface exifInterface = new ExifInterface(imgPath);
            int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            matrix.preRotate(rotationInDegrees);
        } catch (java.io.IOException e) {

        }
        return matrix;
    }

    private static int exifToDegrees(int rotation) {
        if (rotation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else {
            return 0;
        }
    }

}
