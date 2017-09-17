package de.ur.mi.android.baudoku;

import android.app.DialogFragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;


public class ProjectCreateActivity extends AppCompatActivity {

    private static final String EXTRA_ID = "projectId";
    private static final String EXTRA_STATUS = "projectStatus";

    private BaudokuDatabase db;

    private ImageView editImg;
    private EditText editTitle;
    private EditText editAddress;
    private EditText editStart;
    private EditText editClient;
    private EditText editAttendees;

    private Button btnBack;
    private Button btnSave;
    private ImageButton btnGetLocation;

    private LocationManager lm;
    private LocationListener ll;

    private int id;
    private int status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_create);
        initDatabase();
        getExtras();
        getUIElements();
        if (id != -1) {
            insertData();
        }
        initLocationManager();
        initUIElements();
    }

    private void initLocationManager() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("debasdfa", "1");
                connectEditAddress();
                lm.removeUpdates(ll);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("debasdfa", "2");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("debasdfa", "3");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("debasdfa", "4");
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        id = extras.getInt(EXTRA_ID);
        status = extras.getInt(EXTRA_STATUS);
    }

    private void initDatabase() {
        db = new BaudokuDatabase(this);
        db.open();
    }

    private void getUIElements() {
        editImg = (ImageView) findViewById(R.id.add_project_img);
        editTitle = (EditText) findViewById(R.id.add_project_title);
        editAddress = (EditText) findViewById(R.id.add_project_address);
        editStart = (EditText) findViewById(R.id.add_project_start);
        editClient = (EditText) findViewById(R.id.add_project_client);
        editAttendees = (EditText) findViewById(R.id.add_project_attendees);


        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnGetLocation = (ImageButton) findViewById(R.id.add_current_location);
    }

    private void addNewProject(Intent saveIntent) {
        String img = "bitmap";
        String title = editTitle.getText().toString();
        String address = editAddress.getText().toString();
        String start = editStart.getText().toString();
        String client = editClient.getText().toString();
        String attendees = editAttendees.getText().toString();

        ProjectItem newProject = new ProjectItem(img, title, address, start, client, attendees, id, status);

        int newID = db.insertProjectItem(newProject);
        saveIntent.putExtra(EXTRA_ID, newID);
    }

    private void insertData() {
        ProjectItem project = db.getProjectItem(id);
        editTitle.setText(project.title, TextView.BufferType.EDITABLE);
        editAddress.setText(project.address, TextView.BufferType.EDITABLE);;
        editStart.setText(project.start, TextView.BufferType.EDITABLE);;
        editClient.setText(project.client, TextView.BufferType.EDITABLE);;
        editAttendees.setText(project.attendees, TextView.BufferType.EDITABLE);;
    }

    private void initUIElements() {
        editStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerFragment();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInputProjectView();
            }
        });

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
                } catch(SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void connectEditAddress() {
        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Log.d("debasdfa", longitude + " " + latitude);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            editAddress.setText(addresses.get(0).getAddressLine(0));
        } catch(SecurityException e) {
            Log.d("debasdfa", "6");
            e.printStackTrace();
        } catch (java.io.IOException e) {
            Log.d("debasdfa", "5");
            e.printStackTrace();
        }
    }

    private void showDatePickerFragment() {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

    private void saveInputProjectView() {
        Intent saveIntent = new Intent(ProjectCreateActivity.this, ProjectViewActivity.class);
        if (id != -1) {
            db.removeProjectItem(id);
        }
        addNewProject(saveIntent);
        startActivity(saveIntent);
    }
}
