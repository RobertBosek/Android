package de.ur.mi.android.baudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bobi on 15.09.2017.
 */

public class NoteViewActivity extends AppCompatActivity {


    private NoteItem displayNote;
    private static BaudokuDatabase db;

    private Toolbar toolbar;

    private TextView noteDate;
    private TextView noteTemperature;
    private ImageView noteWeatherIcon;
    private ImageView noteImg;
    private ImageButton btnPlayMemo;
    private TextView noteNotes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        initDatabase();
        getDisplayNote();
        getUIElements();
        initUIElements();
    }



    private void initDatabase() {
        db = new BaudokuDatabase(this);
    }

    private void getDisplayNote() {
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt(getString(R.string.intent_extra_key_id_note));
        db.open();
        displayNote = db.getNoteItem(id);
        db.close();
    }

    private void getUIElements() {
        toolbar = (Toolbar) findViewById(R.id.note_view_app_bar);

        TextView noteDate = (TextView) findViewById(R.id.note_view_activity_date);
        noteDate.setText(displayNote.getDate());
        TextView noteTemperature = (TextView) findViewById(R.id.note_view_activity_temperature);
        noteTemperature.setText(displayNote.getTemperature());
        ImageView noteWeatherIcon = (ImageView) findViewById(R.id.note_view_activity_weather);
        noteWeatherIcon.setImageDrawable(getDrawable(R.drawable.add_photo));
        ImageView noteImg = (ImageView) findViewById(R.id.note_view_activity_img);
        ImageHelper.setPic(displayNote.getImgPath(), noteImg);
        btnPlayMemo = (ImageButton) findViewById(R.id.note_view_activity_btnPlayMemo);
        TextView noteNotes = (TextView) findViewById(R.id.note_view_activity_notes);
        noteNotes.setText(displayNote.getNote());
    }

    private void initUIElements() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnPlayMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMemo();
            }
        });
    }

    private void playMemo() {
        if(!displayNote.getMemoPath().equals("")) {
            playdatshit();
        }
    }

    private void playdatshit() {
        Toast.makeText(this, "interesting", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.note_view_menu_edit_note) {
            Intent startNoteCreateActivityIntent = new Intent(NoteViewActivity.this, NoteCreateActivity.class);
            startNoteCreateActivityIntent.putExtra(getString(R.string.intent_extra_key_id_note), displayNote.getId());
            startActivity(startNoteCreateActivityIntent);
        } else if ( id == R.id.note_view_menu_delete_note) {
            makeDeleteDialog();
        } else if ( id == android.R.id.home) {
            showProjectView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_delete_note)
                .setMessage(R.string.dialog_text_delete_note)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.open();
                        db.removeProjectItem(displayNote.getId());
                        db.close();
                        showProjectView();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void showProjectView() {
        Intent startProjectViewActivityIntent = new Intent(NoteViewActivity.this, ProjectViewActivity.class);
        startProjectViewActivityIntent.putExtra(getString(R.string.intent_extra_key_id_project), displayNote.getProjectID());
        startActivity(startProjectViewActivityIntent);
        finish();
    }
}
