package de.ur.mi.android.baudoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AppStartActivity extends AppCompatActivity {


    private ImageButton btnProjectList;
    private ImageButton btnNewProject;
    private ImageButton btnAppInfo;

    private TextView bauDokuTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        getUIElements();
        initUIElements();
    }

    private void getUIElements() {
        bauDokuTextView = (TextView) findViewById(R.id.app_start_activity_baudoku);
        btnProjectList = (ImageButton) findViewById(R.id.project_start_activity_project_list_btn);
        btnNewProject = (ImageButton) findViewById(R.id.project_start_activity_new_project_btn);
        btnAppInfo = (ImageButton) findViewById(R.id.project_start_activity_app_info_btn);

    }

    private void initUIElements() {

        btnProjectList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startProjectListActivityIntent = new Intent(AppStartActivity.this, ProjectListActivity.class);
                startActivity(startProjectListActivityIntent);
            }
        });

        btnNewProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startCreateProjectActivityIntent = new Intent(AppStartActivity.this, ProjectCreateActivity.class);
                startCreateProjectActivityIntent.putExtra(getString(R.string.intent_extra_key_id_project), -1);
                startActivity(startCreateProjectActivityIntent);
            }
        });

        btnAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startAppInfoActivityIntent = new Intent(AppStartActivity.this, AppInfoActivity.class);
                startActivity(startAppInfoActivityIntent);
            }
        });




    }
}
