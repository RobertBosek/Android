package de.ur.mi.android.baudoku;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Marlene on 24.09.2017.
 */

public class Utils {

    private static int sTheme;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_PINK = 0;

    public static void changeToTheme(Activity activity, int theme){
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));

    }
    public static void onActivityCreateSetTheme(Activity activity){
        switch (sTheme){
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_PINK:
                activity.setTheme(R.style.GirlTheme);
                break;


        }
    }

}
