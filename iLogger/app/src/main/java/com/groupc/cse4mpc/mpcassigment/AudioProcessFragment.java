package com.groupc.cse4mpc.mpcassigment;

import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
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
import com.groupc.cse4mpc.mpcassigment.dao.AudioDataSource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by junqi on 21/10/15.
 */
public class AudioProcessFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    AudioDataSource dataSource;
    MediaRecorder mediaRecorder;
    EditText editAudioDescription;
    private boolean startRecordFlag = true;
    public AudioProcessFragment(){}

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
        dataSource = new AudioDataSource(this.getContext());
        dataSource.open();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_audioprocess, container, false);
        final Button btnRecord = (Button)rootView.findViewById(R.id.btnAudioRecord);
        editAudioDescription = (EditText)rootView.findViewById(R.id.editAudioDescription);
        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onRecord(startRecordFlag);
                if(startRecordFlag){
                    btnRecord.setText("Stop recording");
                }else{
                    btnRecord.setText("Start recording");
                }
                startRecordFlag = !startRecordFlag;
            }
        });
        return rootView;
    }

    public void onRecord(boolean start){
        if(start){
            startRecording();
        }else{
            stopRecording();
        }
    }

    public void startRecording(){
        //Location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        final String tLocation = "latitude: " + latitude + " ,longitude: " + longitude + "\n";
        //TimeStamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String currentTimeStamp = dateFormat.format(new Date()) + "\n"; // Find todays date
        //Description
        String description = editAudioDescription.getText().toString();

        //File directory
        File audioStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC), "MyRecordings");
        // Create the storage directory if it does not exist
        if (!audioStorageDir.exists()) {
            if (!audioStorageDir.mkdirs()) {
                Log.d("MyRecordings", "failed to create directory");
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(audioStorageDir.getPath() + File.separator +
                "AUDIO_" + timeStamp + ".3gp");

        //save to database
        dataSource.createAudio(mediaFile.getAbsolutePath(),description,currentTimeStamp,tLocation);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(mediaFile.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try{
            mediaRecorder.prepare();
        }catch (Exception e)
        {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mediaRecorder.start();
    }

    public void stopRecording(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
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
