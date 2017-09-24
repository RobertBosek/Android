package de.ur.mi.android.baudoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class AppSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStyle1;
    private Button btnStyle2;
    private TextView settingsTextView;
    private TextView styleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activitiy_app_settings);
        findViewById(R.id.project_style_btn_1).setOnClickListener(this);
        findViewById(R.id.project_style_btn_2).setOnClickListener(this);
    }
        @Override
                public void onClick(View view){

            switch (view.getId()){
                case R.id.project_style_btn_1:
                        Utils.changeToTheme(this, Utils.THEME_DEFAULT);
                break;
                case R.id.project_style_btn_2:
                    Utils.changeToTheme(this, Utils.THEME_PINK);
            }

        }




       /* getUIElements();
        initUIElements();
    }
    private void getUIElements() {
        settingsTextView = (TextView) findViewById(R.id.app_acivity_style);
        styleTextView = (TextView) findViewById(R.id.change_design);
        btnStyle1 = (Button) findViewById(R.id.project_style_btn_1);
        btnStyle2 = (Button) findViewById(R.id.project_style_btn_2);

    }
    private void initUIElements() {

        btnStyle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startProjectListActivityIntent = new Intent(AppSettingsActivity.this, ProjectListActivity.class);
                startActivity(startProjectListActivityIntent);
            }
        });

        btnStyle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startCreateProjectActivityIntent = new Intent(AppSettingsActivity.this, ProjectCreateActivity.class);
                startCreateProjectActivityIntent.putExtra(getString(R.string.intent_extra_key_id_project), -1);
                startActivity(startCreateProjectActivityIntent);
            }
        });*/


}

    @Override
    public void onClick(View v) {

    }

