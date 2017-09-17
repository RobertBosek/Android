package de.ur.mi.android.baudoku;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.Manifest;


public class ProjectCreateActivity extends AppCompatActivity {

    private static final String EXTRA_ID = "projectId";
    private static final String EXTRA_STATUS = "projectStatus";

    private ProjectItem editProject;
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

    private String img;
    private String title;
    private String address;
    private String start;
    private String client;
    private String attendees;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_create);
        initDatabase();
        getExtras();
        getUIElements();
        if (id == -1) {
            editProject = new ProjectItem(id);
        } else {
            editProject = db.getProjectItem(id);
            insertData();
        }
        initListeners();
    }

    private void initDatabase() {
        db = new BaudokuDatabase(this);
        db.open();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        id = extras.getInt(EXTRA_ID);
        status = extras.getInt(EXTRA_STATUS);
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

    private void insertData() {
        //img
        editTitle.setText(editProject.getTitle(), TextView.BufferType.EDITABLE);
        editAddress.setText(editProject.getAddress(), TextView.BufferType.EDITABLE);;
        editStart.setText(editProject.getStart(), TextView.BufferType.EDITABLE);;
        editClient.setText(editProject.getClient(), TextView.BufferType.EDITABLE);;
        editAttendees.setText(editProject.getAttendees(), TextView.BufferType.EDITABLE);;
    }

    private void initListeners() {
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
                checkPermissionsAndAvailability();
            }
        });
    }


    private void checkPermissionsAndAvailability() {
        checkPermissions();
        initLocationManager();
        checkAvailability();
    }

    private void checkPermissions() {
        int internetPermission = this.checkSelfPermission(android.Manifest.permission.INTERNET);
        int locationFinePermission = this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        int locationCoarsePermission = this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        ArrayList<String> deniedPermissions = new ArrayList<String>();
        if (internetPermission == PackageManager.PERMISSION_DENIED) {
            deniedPermissions.add(android.Manifest.permission.INTERNET);
        }
        if (locationFinePermission == PackageManager.PERMISSION_DENIED) {
            deniedPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (locationCoarsePermission == PackageManager.PERMISSION_DENIED) {
            deniedPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        int size = deniedPermissions.size();
        if (size > 0) {
            this.requestPermissions(deniedPermissions.toArray(new String[size]), 0);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permissssssss", "internet granted");

                } else {
                    Log.d("permissssssss", "internet denied");
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void checkAvailability() {
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
                makeLocationDialog();
            }
        };
    }

    private void makeLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_dialog_title)
                .setMessage(R.string.location_dialog_text)
                .setPositiveButton(R.string.settings_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                })
                .setNegativeButton(R.string.ignore_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
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
            makeInternetDialog();
            Log.d("debasdfa", "5");
            e.printStackTrace();
        }
    }

    private void makeInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.internet_dialog_title)
                .setMessage(R.string.internet_dialog_text)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }



    private void saveInputProjectView() {
        img = "bitmap";
        title = editTitle.getText().toString();
        address = editAddress.getText().toString();
        start = editStart.getText().toString();
        client = editClient.getText().toString();
        attendees = editAttendees.getText().toString();

        if (checkInput()) {
            editProject.setImg(img);
            editProject.setTitle(title);
            editProject.setAddress(address);
            editProject.setStart(start);
            editProject.setClient(client);
            editProject.setAttendees(attendees);
            if (id != -1) {
                db.updateProjectItem(editProject);
            } else {
                id = db.insertProjectItem(editProject);
            }
        }
    }

    private boolean checkInput() {
        if (!title.equals("") && !address.equals("") && !start.equals("")) {
            return true;
        } else {
            makeNecessaryInputDialog();
            return false;
        }
    }

    private void makeNecessaryInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.input_dialog_title)
                .setMessage(R.string.input_dialog_text)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void showDatePickerFragment() {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

}
