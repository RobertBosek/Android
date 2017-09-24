package de.ur.mi.android.baudoku;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class NoteCreateActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_AUDIO = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 10;

    private NoteItem editNote;
    private BaudokuDatabase db;
    private MediaRecorder recorder;
    private MediaPlayer player;

    WeatherIconAdapter adapter;
    private Spinner spinner;
    private EditText editDate;
    private EditText editTemperature;
    private ImageView editImg;
    private EditText editNotes;

    private Button btnBack;
    private Button btnSave;
    private ImageButton btnGetWeather;
    private ImageButton btnMemoPlay;
    private ImageButton btnMemoAdmin;

    private Integer[] drawables = new Integer[]{R.drawable.ic_info, R.drawable.delete_01, R.drawable.cancel_02};
    private int id;
    private int projectId;
    private String tempImgPath;
    private String imgPath = null;
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
        initUIElements();
        initDatabase();
        getExtras();

        if (id == -1) {
            editNote = new NoteItem(id, projectId);
        } else {
            db.open();
            editNote = db.getNoteItem(id);
            db.close();
            insertData();
        }
    }

    public void getUIElements() {
        editDate = (EditText) findViewById(R.id.date_edit);
        DateFormat df = new SimpleDateFormat("dd.MM.yy");
        editDate.setText(df.format(Calendar.getInstance().getTime()));

        editTemperature = (EditText) findViewById(R.id.note_create_activity_add_note_temperature);
        spinner = (Spinner) findViewById(R.id.note_create_activity_add_note_spinner_weather);
        editImg = (ImageView) findViewById(R.id.note_create_activity_add_note_img);
        editNotes = (EditText) findViewById(R.id.note_create_activity_add_note_notes);

        btnBack = (Button) findViewById(R.id.note_create_btnBack);
        btnSave = (Button) findViewById(R.id.note_create_btnSave);
        btnGetWeather = (ImageButton) findViewById(R.id.btnAuto_weather);
        btnMemoPlay = (ImageButton) findViewById(R.id.btnPlayMemo);
        btnMemoAdmin = (ImageButton) findViewById(R.id.btnGetMemo);
    }

    private void initUIElements() {
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

        adapter = new WeatherIconAdapter(this, new ArrayList<Integer>(Arrays.asList(drawables)));
        spinner.setAdapter(adapter);
        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeather();
            }
        });
        editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionsCamera();
            }
        });
        btnMemoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMemo();
            }
        });
        btnMemoPlay.setEnabled(false);
        btnMemoAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionsRecording();
            }
        });
    }

    private void checkPermissionsRecording() {
        String cameraPermission = android.Manifest.permission.RECORD_AUDIO;
        int permissionDenied = PackageManager.PERMISSION_DENIED;
        if (ActivityCompat.checkSelfPermission(this, cameraPermission) == permissionDenied) {
            this.requestPermissions(new String[]{cameraPermission}, REQUEST_PERMISSION_AUDIO);
        } else {
            setupMemoRecording();
        }
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
        imgPath = editNote.getImgPath();
        ImageHelper.setPic(imgPath, editImg);

        int spinnerPosition = adapter.getPosition(Integer.valueOf(editNote.getWeather()));
        spinner.setSelection(spinnerPosition);

        editNotes.setText(editNote.getNote(), TextView.BufferType.EDITABLE);
        memoPath = editNote.getMemoPath();
        if (!memoPath.equals("")) {
            btnMemoPlay.setEnabled(true);
            btnMemoAdmin.setImageDrawable(getDrawable(R.drawable.ic_delete));
            btnMemoAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteMemo();
                }
            });
        }
    }

    private void setupMemoRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File audioFile = null;
        try {
            audioFile = createFile("audio");
        } catch (IOException ex) {
            Toast.makeText(this, R.string.toast_on_failed_filecreation, Toast.LENGTH_SHORT).show();
        }
        if (audioFile != null) {
            recorder.setOutputFile(memoPath);
            try {
                recorder.prepare();
                btnMemoAdmin.setImageDrawable(getDrawable(R.drawable.ic_record));
                btnMemoAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recordMemo();
                    }
                });
            } catch (IOException e) {
                Toast.makeText(this, R.string.toast_on_failed_recorder, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void recordMemo() {
        recorder.start();
        btnMemoAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
        btnMemoAdmin.setImageDrawable(getDrawable(R.drawable.ic_stop));
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        btnMemoPlay.setEnabled(true);
        btnMemoAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMemo();
            }
        });
        btnMemoAdmin.setImageDrawable(getDrawable(R.drawable.ic_delete));
    }

    private void deleteMemo() {
        File toDelete = new File(memoPath);

        if (toDelete.delete()) {
            memoPath = "";
            btnMemoPlay.setEnabled(false);
            btnMemoAdmin.setImageDrawable(getDrawable(R.drawable.ic_recorder));
            btnMemoAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermissionsRecording();
                }
            });
        }

    }

    private void playMemo() {
        player = new MediaPlayer();
        try {
            player.setDataSource(memoPath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Toast.makeText(this, R.string.toast_on_failed_player, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerFragment() {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

    private void getWeather() {

    }


    private void checkPermissionsCamera() {
        String cameraPermission = android.Manifest.permission.CAMERA;
        int permissionDenied = PackageManager.PERMISSION_DENIED;
        if (ActivityCompat.checkSelfPermission(this, cameraPermission) == permissionDenied) {
            this.requestPermissions(new String[]{cameraPermission}, REQUEST_PERMISSION_CAMERA);
        } else {
           getImage();
        }
    }

    private void getImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createFile("image");
            } catch (IOException ex) {
                Toast.makeText(this, R.string.toast_on_failed_filecreation, Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "de.ur.mi.android.baudoku.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createFile(String filetype) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = null;
        if (filetype.equals("image")){
            file = File.createTempFile(timeStamp, ".jpg", storageDir);
            tempImgPath = imgPath;
            imgPath = file.getAbsolutePath();
        } else if (filetype.equals("audio")){
            file = File.createTempFile(timeStamp, ".amr", storageDir);
            memoPath = file.getAbsolutePath();
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageHelper.setPic(imgPath, editImg);
            if (tempImgPath != null) {
                File toDelete = new File(tempImgPath);
                toDelete.delete();
            }
        } else {
            File toDelete = new File(imgPath);
            toDelete.delete();
            imgPath = tempImgPath;
            Toast.makeText(this, R.string.toast_on_failed_filecreation, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage();
                }
            }
            case REQUEST_PERMISSION_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupMemoRecording();
                }
            }
        }
    }



    private void saveInputNoteView() {
        date = editDate.getText().toString();
        temperature = editTemperature.getText().toString();
        weatherIcon = spinner.getSelectedItem().toString();
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

    @Override
    public void onBackPressed() {
        if (imgPath != null) {
            if (!imgPath.equals(editNote.getImgPath())) {
                File toDelete = new File(imgPath);
                toDelete.delete();
            }
        }
        if (memoPath != null) {
            if (!memoPath.equals(editNote.getMemoPath())) {
                File toDelete = new File(memoPath);
                toDelete.delete();
            }
        }
        if (player != null) {
            player.release();
            player = null;
        }
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        super.onBackPressed();
    }

    private static class WeatherIconAdapter extends ArrayAdapter<Integer> {
        private ArrayList<Integer> icons;
        private Context context;

        public WeatherIconAdapter(Context context, ArrayList<Integer> icons) {
            super(context, R.layout.item_weather_spinner, icons);
            this.context = context;
            this.icons = icons;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.item_weather_spinner, null);
            }
            ImageView weatherIcon = (ImageView) v.findViewById(R.id.weather_spinner_icon);
            weatherIcon.setImageDrawable(context.getResources().getDrawable(icons.get(position)));
            return v;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.item_weather_spinner, null);
            }
            ImageView weatherIcon = (ImageView) v.findViewById(R.id.weather_spinner_icon);
            weatherIcon.setImageDrawable(context.getResources().getDrawable(icons.get(position)));
            return v;
        }

    }
}