package com.groupc.cse4mpc.mpcassigment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.groupc.cse4mpc.mpcassigment.dao.MyWiFi;
import com.groupc.cse4mpc.mpcassigment.dao.WiFiDataSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by junqi on 20/10/15.
 */
public class WiFiScansFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private TextView tvWifiHotspot;
    private ListView lvWifiHistory;

    private WiFiDataSource dataSource;

    private WifiManager mWifiManager;
    private BroadcastReceiver mWifiReceiver;

    public WiFiScansFragment(){ }

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
        dataSource = new WiFiDataSource(this.getContext());
        dataSource.open();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wifiscans, container, false);
        tvWifiHotspot = (TextView) rootView.findViewById(R.id.tvWifiHotspots);
        /**
         * Accessing the Wi-Fi Manager
         */
        mWifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

        /**
         * Monitoring and changing Wi-Fi state
         */
        if(!mWifiManager.isWifiEnabled()) {
            if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                mWifiManager.setWifiEnabled(true);
            }
        }

        Button btnScan = (Button) rootView.findViewById(R.id.btnWifiScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWifiScan();
            }
        });

        //Use the adapter to show the elements in a listView
        lvWifiHistory = (ListView) rootView.findViewById(R.id.lvWifiHistory);

        List<MyWiFi> myWiFis = dataSource.getAllWiFis();
        String[] mFrom = new String[]{"id","time"};
        int[] mTo = new int[]{R.id.itemTitle1,R.id.itemTitle2};

        List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
        Map<String,Object> mMap = null;
        for(MyWiFi t: myWiFis) {
            mMap = new HashMap<String, Object>();
            mMap.put("data",t);
            mMap.put("id",t.getId());
            mMap.put("time",t.getTime());
            mList.add(mMap);
        }
        //Create Adapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this.getContext(),mList,R.layout.listview_item,mFrom,mTo);
        lvWifiHistory.setAdapter(simpleAdapter);

        lvWifiHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
                MyWiFi pWiFi = (MyWiFi) map.get("data");
                tvWifiHotspot.setText(pWiFi.getTime() + pWiFi.getLocation() + pWiFi.getSummary());
            }
        });

        //send through bluetooth
        lvWifiHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
                MyWiFi pWiFi = (MyWiFi) map.get("data");
                String wifiInfo = pWiFi.getTime() + pWiFi.getLocation() + pWiFi.getSummary();

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, wifiInfo);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share"));

                return false;
            }
        });
        return rootView;
    }

    public void startWifiScan() {
        //Location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        final String tLocation = "latitude: " + latitude + " ,longitude: " + longitude + "\n";
        //TimeStamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String currentTimeStamp = dateFormat.format(new Date()) + "\n"; // Find todays date

        mWifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> results = mWifiManager.getScanResults();
                String found = new String("SCAN RESULT:\n");

                for(ScanResult result : results){
                    found = found.concat(new String("SSID:"+result.SSID+";\nBSSID:"+result.BSSID+";\nRSSI:"+result.level + "\n"));
                }
                tvWifiHotspot.setText(found);
                //save to database
                dataSource.createWiFi(found,currentTimeStamp,tLocation);
                //Location
                tvWifiHotspot.append(tLocation);
                //TimeStamp
                tvWifiHotspot.append(currentTimeStamp);

                context.unregisterReceiver(this);
            }
        };
        getActivity().registerReceiver(mWifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
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
