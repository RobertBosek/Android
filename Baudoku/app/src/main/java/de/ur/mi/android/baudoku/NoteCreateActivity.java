package de.ur.mi.android.baudoku;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoteCreateActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_LOCATION = 0;
    private static final int REQUEST_PERMISSION_PICTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 10;

    private NoteItem editNote;
    private BaudokuDatabase db;

    private EditText editDate;
    private EditText editTemperature;
    private EditText editWeather;
    private ImageView editImg;
    private EditText editNotes;

    private Button btnBack;
    private Button btnSave;
    private ImageButton btnGetWeather;
    private ImageButton btnMemoPlay;
    private ImageButton btnMemoDelete;
    private ImageButton btnGetMemo;

    private int id;
    private int projectId;
    private String tempImgPath;
    private String imgPath;
    private String tempMemoPath;
    private String memoPath;

    private String date;
    private String temperature;
    private String weatherIcon;
    private String notes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_create);
        getUIElements();
        initListeners();
        initDatabase();
        getExtras();

        if (id == -1) {
            editNote = new NoteItem(id, projectId);
        } else {
            db.open();
            editNote = db.getNoteItem(id);
            db.close();
            Log.d("lel", "lel");
            insertData();
            Log.d("lel", "lel");
        }
    }

    public void getUIElements() {
        editDate = (EditText) findViewById(R.id.date_edit);
        DateFormat df = new SimpleDateFormat("dd.mm.yyyy");
        editDate.setText(df.format(Calendar.getInstance().getTime()));

        editTemperature = (EditText) findViewById(R.id.note_create_activity_add_note_temperature);
        editWeather = (EditText) findViewById(R.id.note_create_activity_add_note_weather);
        editImg = (ImageView) findViewById(R.id.note_create_activity_add_note_img);
        editNotes = (EditText) findViewById(R.id.note_create_activity_add_note_notes);

        btnBack = (Button) findViewById(R.id.note_create_btnBack);
        btnSave = (Button) findViewById(R.id.note_create_btnSave);
        btnGetWeather = (ImageButton) findViewById(R.id.btnAuto_weather);
        btnMemoPlay = (ImageButton) findViewById(R.id.btnPlayMemo);
        btnMemoDelete = (ImageButton) findViewById(R.id.btnDeleteMemo);
        btnGetMemo = (ImageButton) findViewById(R.id.btnGetMemo);
    }

    private void initListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInputNoteView();
            }
        });
        editDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerFragment();
                }
            }
        });
        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeather();
            }
        });
        editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });
        btnMemoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMemo();
            }
        });
        btnMemoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMemo();
            }
        });
        btnGetMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMemo();
            }
        });
    }

    private void initDatabase() {
        db = new BaudokuDatabase(this);
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        id = extras.getInt(getString(R.string.intent_extra_key_id_note));
        projectId = extras.getInt(getString(R.string.intent_extra_key_id_project));
    }

    private void insertData() {
        editDate.setText(editNote.getDate(), TextView.BufferType.EDITABLE);
        editTemperature.setText(editNote.getTemperature(), TextView.BufferType.EDITABLE);
        editWeather.setText(editNote.getWeather(), TextView.BufferType.EDITABLE);
        imgPath = editNote.getImgPath();
        ImageHelper.setPic(imgPath, editImg);
        editNotes.setText(editNote.getNote(), TextView.BufferType.EDITABLE);
        memoPath = editNote.getMemoPath();
    }

    private void getMemo() {

    }

    private void deleteMemo() {

    }

    private void playMemo() {

    }

    private void showDatePickerFragment() {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

    private void getImage() {
    }

    private void getWeather() {

    }


    private void saveInputNoteView() {
        date = editDate.getText().toString();
        temperature = editTemperature.getText().toString();
        weatherIcon = editWeather.getText().toString();
        notes = editNotes.getText().toString();

        if (checkInput()) {
            if (imgPath != null) {
                editNote.setImgPath(imgPath);
            } else {
                editNote.setImgPath("");
            }
            if (memoPath != null) {
                editNote.setMemoPath(memoPath);
            } else {
                editNote.setMemoPath("");
            }

            editNote.setDate(date);
            editNote.setTemperature(temperature);
            editNote.setWeather(weatherIcon);
            editNote.setNotes(notes);

            db.open();
            if (id != -1) {
                db.updateNoteItem(editNote);
            } else {
                id = db.insertNoteItem(editNote);
            }
            db.close();
            Log.d("lel", String.valueOf(id));
            Intent startNoteViewActivityIntent = new Intent(NoteCreateActivity.this, NoteViewActivity.class);
            startNoteViewActivityIntent.putExtra(getString(R.string.intent_extra_key_id_note), id);
            startActivity(startNoteViewActivityIntent);
            finish();
        }
    }

    private boolean checkInput() {
        if (!date.equals("" ) && !temperature.equals("") && !weatherIcon.equals("")) {
            return true;
        } else {
            makeNecessaryInputDialog();
            return false;
        }
    }
    private void makeNecessaryInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_missing_input)
                .setMessage(R.string.dialog_text_missing_input_note)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

}