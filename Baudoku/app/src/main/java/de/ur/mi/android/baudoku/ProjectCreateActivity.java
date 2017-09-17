package de.ur.mi.android.baudoku;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private static final int REQUEST_PERMISSION = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String EXTRA_ID = "projectId";

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

    private String imgPath;
    private String title;
    private String address;
    private String start;


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

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    private void insertData() {
        imgPath = editProject.getImg();
        setPic();
        editTitle.setText(editProject.getTitle(), TextView.BufferType.EDITABLE);
        editAddress.setText(editProject.getAddress(), TextView.BufferType.EDITABLE);;
        editStart.setText(editProject.getStart(), TextView.BufferType.EDITABLE);;
        editClient.setText(editProject.getClient(), TextView.BufferType.EDITABLE);;
        editAttendees.setText(editProject.getAttendees(), TextView.BufferType.EDITABLE);;
    }

    private void initListeners() {
        editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
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
                setLocationListener();
            }
        });

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

    private void getImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, R.string.create_file_failed_toast, Toast.LENGTH_SHORT).show();
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
        imgPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {
        int viewSize = editImg.getWidth();
        editImg.getLayoutParams().height = viewSize;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/viewSize, photoH/viewSize);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
        editImg.setImageBitmap(bitmap);
    }

    private void setLocationListener() {
        checkPermissions();
    }

    private void checkPermissions() {
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
            this.requestPermissions(deniedPermissions.toArray(new String[size]), REQUEST_PERMISSION);
        } else {
            checkAvailability();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAvailability();
                }
            }
        }
    }

    private void checkAvailability() {
        if (isOnline()) {
            connectLocationListenerToManager();
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
        builder.setTitle(R.string.internet_dialog_title)
                .setMessage(R.string.internet_dialog_text)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void connectLocationListenerToManager() {
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        } catch (SecurityException e) {

        }
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

    private void setAddress() {
        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String addressString = addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getAddressLine(1);
            editAddress.setText(addressString);
        } catch(SecurityException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            makeInternetDialog();
            e.printStackTrace();
        }
    }

    private void saveInputProjectView() {
        title = editTitle.getText().toString();
        address = editAddress.getText().toString();
        start = editStart.getText().toString();
        String client = editClient.getText().toString();
        String attendees = editAttendees.getText().toString();


        if (checkInput()) {
            if (imgPath != null) {
                editProject.setImg(imgPath);
            } else {
                editProject.setImg("");
            }
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
            Intent i = new Intent(ProjectCreateActivity.this, ProjectViewActivity.class);
            i.putExtra(EXTRA_ID, id);
            startActivity(i);
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