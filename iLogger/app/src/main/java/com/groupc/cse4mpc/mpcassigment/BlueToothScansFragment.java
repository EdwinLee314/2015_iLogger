package com.groupc.cse4mpc.mpcassigment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.groupc.cse4mpc.mpcassigment.dao.BlueToothDataSource;
import com.groupc.cse4mpc.mpcassigment.dao.MyBlueTooth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by junqi on 20/10/15.
 */
public class BlueToothScansFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private TextView tvBlueTooth;
    private ListView lvBlueToothHistory;

    private BlueToothDataSource dataSource;
    BluetoothAdapter mBluetooth = null;
    BroadcastReceiver mBluetoothReceiver;

    public BlueToothScansFragment(){}

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
        dataSource = new BlueToothDataSource(this.getContext());
        dataSource.open();
        //

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bluetoothscans, container, false);
        tvBlueTooth = (TextView) rootView.findViewById(R.id.tvBlueTooth);
        /**
         * Accessing the BlueTooth Adapter
         */
        this.mBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(mBluetooth == null)
        {
            Toast.makeText(getActivity(), "Device does not support bluetooth", Toast.LENGTH_SHORT).show();
        }

            Button btnScan = (Button) rootView.findViewById(R.id.btnBlueToothScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBlueToothScan();
            }
        });

        //Use the adapter to show the elements in a listView
        lvBlueToothHistory = (ListView) rootView.findViewById(R.id.lvBlueToothHistory);

        List<MyBlueTooth> myBlueTooth = dataSource.getAllBlueTooths();
        String[] mFrom = new String[]{"id","time"};
        int[] mTo = new int[]{R.id.itemTitle1,R.id.itemTitle2};

        List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
        Map<String,Object> mMap = null;
        for(MyBlueTooth t: myBlueTooth) {
            mMap = new HashMap<String, Object>();
            mMap.put("data",t);
            mMap.put("id",t.getId());
            mMap.put("time",t.getTime());
            mList.add(mMap);
        }
        //Create Adapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this.getContext(),mList,R.layout.listview_item,mFrom,mTo);
        lvBlueToothHistory.setAdapter(simpleAdapter);

        lvBlueToothHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
                MyBlueTooth pBlueTooth = (MyBlueTooth) map.get("data");
                tvBlueTooth.setText(pBlueTooth.getTime() + pBlueTooth.getLocation() + pBlueTooth.getSummary());
            }
        });
        //send through bluetooth
        lvBlueToothHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
                MyBlueTooth pBlueTooth = (MyBlueTooth) map.get("data");
                String blueToothInfo = pBlueTooth.getTime() + pBlueTooth.getLocation() + pBlueTooth.getSummary();

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, blueToothInfo);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share"));

                return false;
            }
        });
        return rootView;
    }
    public void startBlueToothScan(){
        //Location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        final String tLocation = "latitude: " + latitude + " ,longitude: " + longitude + "\n";
        //TimeStamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String currentTimeStamp = dateFormat.format(new Date()) + "\n"; // Find todays date


            mBluetoothReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String found = new String("SCAN RESULT:\n");
                    /*
                    List<BluetoothDevice> remoteDevices = intent.getParcelableArrayListExtra(BluetoothDevice.EXTRA_DEVICE);
                    for(BluetoothDevice t:remoteDevices){
                        found = found.concat(new String("Name:"+t.getName()+";\nAddress:"+t.getAddress()+"\n"));
                    }
                    */
                    BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    found = found.concat(new String("Name:"+remoteDevice.getName()+";\nAddress:"+remoteDevice.getAddress()+"\n"));

                    tvBlueTooth.setText(found);
                    //save to database
                    dataSource.createBlueTooth(found, currentTimeStamp,tLocation);

                    tvBlueTooth.append(tLocation);
                    tvBlueTooth.append(currentTimeStamp);


                    context.unregisterReceiver(this);
                }
            };
            getActivity().registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        mBluetooth.startDiscovery();
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
        Toast.makeText(this.getContext(), "Connected to Google Play Services.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this.getContext(), "Connection suspended...", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this.getContext(), "Failed to connect...", Toast.LENGTH_SHORT).show();
    }
}
