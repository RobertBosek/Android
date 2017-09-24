package de.ur.mi.android.baudoku;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ProjectCreateActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_LOCATION = 0;
    private static final int REQUEST_PERMISSION_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 10;

    private ProjectItem editProject;
    private BaudokuDatabase db;

    private ImageView editImg;
    private EditText editTitle;
    private EditText editStart;
    private EditText editAddress;
    private EditText editCity;
    private EditText editClient;
    private EditText editAttendees;

    private Button btnBack;
    private Button btnSave;
    private ImageButton btnGetLocation;

    private LocationManager lm;
    private LocationListener ll;

    private int id;
    private String tempPath;
    private String imgPath = null;
    private String title;
    private String start;
    private String address;
    private String city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_create);
        getUIElements();
        initListeners();
        initDatabase();
        getExtras();

        if (id == -1) {
            editProject = new ProjectItem(id);
        } else {
            db.open();
            editProject = db.getProjectItem(id);
            db.close();
            insertData();
        }
    }

    @Override
    public void onBackPressed() {
        if (imgPath != null) {
            if (!imgPath.equals(editProject.getImgPath())) {
                File toDelete = new File(imgPath);
                toDelete.delete();
            }
        }
        super.onBackPressed();
    }

    private void initDatabase() {
        db = new BaudokuDatabase(this);
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        id = extras.getInt(getString(R.string.intent_extra_key_id_project));
    }

    private void insertData() {
        imgPath = editProject.getImgPath();
        ImageHelper.setPic(imgPath, editImg);
        editTitle.setText(editProject.getTitle(), TextView.BufferType.EDITABLE);
        editStart.setText(editProject.getStart(), TextView.BufferType.EDITABLE);
        editAddress.setText(editProject.getAddress(), TextView.BufferType.EDITABLE);
        editCity.setText(editProject.getCity(), TextView.BufferType.EDITABLE);;
        editClient.setText(editProject.getClient(), TextView.BufferType.EDITABLE);
        editAttendees.setText(editProject.getAttendees(), TextView.BufferType.EDITABLE);
    }

    private void getUIElements() {
        editImg = (ImageView) findViewById(R.id.project_create_activity_add_project_img);

        editTitle = (EditText) findViewById(R.id.project_create_activity_add_project_title);
        editStart = (EditText) findViewById(R.id.date_edit);
        editAddress = (EditText) findViewById(R.id.project_create_activity_add_project_address);
        editCity = (EditText) findViewById(R.id.project_create_activity_add_project_city);
        editClient = (EditText) findViewById(R.id.project_create_activity_add_project_client);
        editAttendees = (EditText) findViewById(R.id.project_create_activity_add_project_attendees);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnGetLocation = (ImageButton) findViewById(R.id.project_create_activity_add_current_location);
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
                saveInputProjectView();
            }
        });

        editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionsCamera();
            }
        });
        editStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerFragment();
                }
            }
        });



        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionsLocation();
            }
        });

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setAddress();
                lm.removeUpdates(ll);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                makeLocationDialog();
            }
        };
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
                photoFile = createImageFile();
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(timeStamp, ".jpg", storageDir);
        tempPath = imgPath;
        imgPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageHelper.setPic(imgPath, editImg);
            if (tempPath!= null) {
                File toDelete = new File(tempPath);
                toDelete.delete();
            }
        } else {
            File toDelete = new File(imgPath);
            toDelete.delete();
            imgPath = tempPath;
            Toast.makeText(this, R.string.toast_on_failed_filecreation, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionsLocation() {
        String internetPermission = android.Manifest.permission.INTERNET;
        String connectivityPermission = android.Manifest.permission.ACCESS_NETWORK_STATE;
        String locationFinePermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        String locationCoarsePermission = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        int permissionDenied = PackageManager.PERMISSION_DENIED;
        ArrayList<String> deniedPermissions = new ArrayList<String>();
        if (ActivityCompat.checkSelfPermission(this, internetPermission) == permissionDenied) {
            deniedPermissions.add(internetPermission);
        }
        if (ActivityCompat.checkSelfPermission(this, connectivityPermission) == permissionDenied) {
            deniedPermissions.add(connectivityPermission);
        }
        if (ActivityCompat.checkSelfPermission(this, locationFinePermission) == permissionDenied) {
            deniedPermissions.add(locationFinePermission);
        }
        if (ActivityCompat.checkSelfPermission(this, locationCoarsePermission) == permissionDenied) {
            deniedPermissions.add(locationCoarsePermission);
        }
        int size = deniedPermissions.size();
        if (size > 0) {
            this.requestPermissions(deniedPermissions.toArray(new String[size]), REQUEST_PERMISSION_LOCATION);
        } else {
            checkAvailability();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAvailability();
                }
            }
            case REQUEST_PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage();
                }
            }
        }
    }

    private void checkAvailability() {
        if (isOnline()) {
            requestLocationUpdates();
        } else {
            makeInternetDialog();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    private void makeInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_missing_internet)
                .setMessage(R.string.dialog_text_missing_internet)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void requestLocationUpdates() {
        try {
            Toast.makeText(this, R.string.toast_on_get_location, Toast.LENGTH_SHORT).show();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        } catch (SecurityException e) {

        }
    }

    private void makeLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_get_location)
                .setMessage(R.string.dialog_text_get_location)
                .setPositiveButton(R.string.button_settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                })
                .setNegativeButton(R.string.button_ignore, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void setAddress() {
        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            editAddress.setText(addresses.get(0).getAddressLine(0));
            editCity.setText(addresses.get(0).getAddressLine(1));
        } catch(SecurityException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            makeInternetDialog();
            e.printStackTrace();
        }
    }

    private void saveInputProjectView() {

        title = editTitle.getText().toString();
        start = editStart.getText().toString();
        address = editAddress.getText().toString();
        city = editCity.getText().toString();
        String client = editClient.getText().toString();
        String attendees = editAttendees.getText().toString();

        if (checkInput()) {

            if (imgPath != null) {
                editProject.setImgPath(imgPath);
            } else {
                editProject.setImgPath("");
            }

            editProject.setTitle(title);
            editProject.setStart(start);
            editProject.setAddress(address);
            editProject.setCity(city);
            editProject.setClient(client);
            editProject.setAttendees(attendees);

            db.open();
            if (id != -1) {
                db.updateProjectItem(editProject);
            } else {
                id = db.insertProjectItem(editProject);
            }
            db.close();

            Intent startProjectViewActivityIntent = new Intent(ProjectCreateActivity.this, ProjectViewActivity.class);
            startProjectViewActivityIntent.putExtra(getString(R.string.intent_extra_key_id_project), id);
            startActivity(startProjectViewActivityIntent);
            finish();
        }
    }

    private boolean checkInput() {
        if (!title.equals("" ) && !start.equals("") && !address.equals("") && !city.equals("")) {
            return true;
        } else {
            makeNecessaryInputDialog();
            return false;
        }
    }

    private void makeNecessaryInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_missing_input)
                .setMessage(R.string.dialog_text_missing_input_project)
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