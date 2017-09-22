package de.ur.mi.android.baudoku;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AppInfoActivity extends AppCompatActivity{

    private TextView infoTextHeading;
    private TextView infoTextIntro;
    private TextView infoText01;
    private TextView infoText02;
    private TextView infoText0201;
    private TextView infoText020101;
    private TextView infoText020102;
    private TextView infoText0202;
    private TextView infoText03;
    private TextView infoText04;
    private TextView infoText05;
    private TextView infoTextOutro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        getUIElements();
    }

    private void getUIElements() {
        infoTextHeading = (TextView) findViewById(R.id.app_info_heading);
        infoTextIntro = (TextView) findViewById(R.id.app_info_intro);
        infoText01 = (TextView) findViewById(R.id.app_info_01);
        infoText02 = (TextView) findViewById(R.id.app_info_02);
        infoText0201 = (TextView) findViewById(R.id.app_info_0201);
        infoText020101 = (TextView) findViewById(R.id.app_info_020101);
        infoText020102 = (TextView) findViewById(R.id.app_info_020102);
        infoText0202 = (TextView) findViewById(R.id.app_info_0202);
        infoText03 = (TextView) findViewById(R.id.app_info_03);
        infoText04 = (TextView) findViewById(R.id.app_info_04);
        infoText05 = (TextView) findViewById(R.id.app_info_05);
        infoTextOutro = (TextView) findViewById(R.id.app_info_outro);
    }
}
