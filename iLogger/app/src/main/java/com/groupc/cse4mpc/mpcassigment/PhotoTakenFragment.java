package com.groupc.cse4mpc.mpcassigment;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.groupc.cse4mpc.mpcassigment.dao.PhotoDataSource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by junqi on 21/10/15.
 */
public class PhotoTakenFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    PhotoDataSource dataSource;
    private EditText editPhotoDescription;
    private Uri outputFileUri;
    private static final int TAKE_PICTURE = 0;
    public PhotoTakenFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Connect Google
        buildGoogleApiClient();
        createLocationRequest();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else{
            Toast.makeText(this.getContext(), "Not connected...", Toast.LENGTH_SHORT).show();
        }
        //Connect Database
        dataSource = new PhotoDataSource(this.getContext());
        dataSource.open();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_phototaken, container, false);
        final Button btnTakePhoto = (Button)rootView.findViewById(R.id.btnPhotoTaken);
        editPhotoDescription = (EditText)rootView.findViewById(R.id.editPhotoDescription);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        return rootView;
    }

    public void takePhoto(){
        //Location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        final String tLocation = "latitude: " + latitude + " ,longitude: " + longitude + "\n";
        //TimeStamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String currentTimeStamp = dateFormat.format(new Date()) + "\n"; // Find todays date
        //Description
        String description = editPhotoDescription.getText().toString();


        // Create an output file.
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyPhotos");
        // Create the storage directory if it does not exist
        if (!imageStorageDir.exists()) {
            if (!imageStorageDir.mkdirs()) {
                Log.d("MyPhotos", "failed to create directory");
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imageFile = new File(imageStorageDir.getPath() + File.separator +
                "Photo_" + timeStamp + ".jpg");

        //save to database
        dataSource.createPhoto(imageFile.getAbsolutePath(),description,currentTimeStamp,tLocation);

        outputFileUri = Uri.fromFile(imageFile);

        // Generate the Intent.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        // Launch the camera app.
        startActivityForResult(intent, TAKE_PICTURE);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this.getContext(), "Connected to Google Play Services.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this.getContext(), "Connection suspended...", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(this.getContext(), "Failed to connect...", Toast.LENGTH_SHORT).show();
    }
}
