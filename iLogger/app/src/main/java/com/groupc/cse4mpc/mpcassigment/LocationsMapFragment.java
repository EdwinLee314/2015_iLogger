package com.groupc.cse4mpc.mpcassigment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.groupc.cse4mpc.mpcassigment.dao.LocationDataSource;

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
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.groupc.cse4mpc.mpcassigment.dao.MyLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by junqi on 7/10/15.
 */
public class LocationsMapFragment extends Fragment implements ConnectionCallbacks,OnConnectionFailedListener,LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationDataSource dataSource;
    private SupportMapFragment mSupportMapFragment;
    private Location mLastLocation;
    private ListView lvLocationHistory;

    public LocationsMapFragment(){ }

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
        dataSource = new LocationDataSource(this.getContext());
        dataSource.open();
        //setUpMapIfNeeded();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_locationsmap, container, false);
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mMap = mSupportMapFragment.getMap();
        showLocationsInMap();
        setUpMapIfNeeded();

        Button btnRecord = (Button) rootView.findViewById(R.id.btnRecordCurrentLocation);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordCurrentLocation();
            }
        });
        final TextView textViewNearby = (TextView) rootView.findViewById(R.id.textViewNearby);
        Button btnShow = (Button) rootView.findViewById(R.id.btnNearby);

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addressList = showNearbyPlaces();
                textViewNearby.setText(addressList);
            }
        });
        //Use the adapter to show the elements in a listView
        lvLocationHistory = (ListView) rootView.findViewById(R.id.lvLocationHistory);

        List<MyLocation> myLocations = dataSource.getAllLocations();
        String[] mFrom = new String[]{"id","address"};
        int[] mTo = new int[]{R.id.itemTitle1,R.id.itemTitle2};

        List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
        Map<String,Object> mMap = null;
        for(MyLocation t: myLocations) {
            mMap = new HashMap<String, Object>();
            mMap.put("data",t);
            mMap.put("id",t.getId());
            mMap.put("address",t.getAddress());
            mList.add(mMap);
        }
        //Create Adapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this.getContext(),mList,R.layout.listview_item,mFrom,mTo);
        lvLocationHistory.setAdapter(simpleAdapter);

        lvLocationHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
                MyLocation pLocation = (MyLocation) map.get("data");
                String LocationInfo = "Address:" + pLocation.getAddress() + "\n Latitude:" + pLocation.getLatitude()+"\n Longitude:" + pLocation.getLongitude() +"\n";

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, LocationInfo);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });

        return rootView;

    }
    public String caching(){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        double minDistance = 0.001704;
        String aimAddress = "Can not caching from past data";
        List<MyLocation> myLocations = dataSource.getAllLocations();
        for(MyLocation location: myLocations){
            double longY = location.getLongitude();
            double latX = location.getLatitude();
            String address = location.getAddress();
            double distance = calculateDistance(latitude, latX, longitude, longY);
            if (distance < minDistance){
                aimAddress = address;
                minDistance = distance;
            }
        }
        return aimAddress;
    }

    public double calculateDistance(double x1, double x2, double y1, double y2){
        return Math.sqrt ((x1-x2) * (x1 - x2) + (y1 - y2) * ( y1 - y2));

    }
    public String showNearbyPlaces(){

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Geocoder geocoder= new Geocoder(this.getContext(), Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 10);
            if (addresses != null) {
                StringBuilder addressStrList = new StringBuilder();
                for(Address address:addresses){
                    StringBuilder strAddress = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        if(i == 0) {
                            strAddress.append("Location: \n");
                        }
                        strAddress.append(address.getAddressLine(i)).append("\n");
                    }
                    addressStrList.append(strAddress.toString()).append("\n");
                }
                return addressStrList.toString();
            } else {
                Toast.makeText(this.getContext(), "No Location found..!", Toast.LENGTH_LONG).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), "Could not get address..!", Toast.LENGTH_LONG).show();
            return "Caching" + caching();
        }
        return "No Nearby Places ";
    }

    public void recordCurrentLocation(){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        String address = loadAddress(mLastLocation);
        dataSource.createLocation(latitude, longitude, address);
        Toast.makeText(this.getContext(), "Current Location has been recorded", Toast.LENGTH_LONG).show();
    }

    public String loadAddress(Location location){
        Geocoder geocoder= new Geocoder(this.getContext(), Locale.ENGLISH);
        if(mLastLocation != null) {
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null) {
                    Address fetchedAddress = addresses.get(0);
                    StringBuilder strAddress = new StringBuilder();
                    for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                        strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                    }
                    Toast.makeText(this.getContext(), "I am at" + strAddress.toString(), Toast.LENGTH_LONG).show();
                    return strAddress.toString();
                } else {
                    Toast.makeText(this.getContext(), "No Location found..!", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this.getContext(), "Could not get address..!", Toast.LENGTH_LONG).show();
            }
        }else{
            //insert codes here for display text when it couldn’t get the location.
            Toast.makeText(this.getContext(), "The application can't get the location", Toast.LENGTH_LONG).show();
        }
        return "No Address";
    }

    public void showLocationsInMap(){
        if(mMap != null){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //Past Locations
            List<MyLocation> pastLocations = dataSource.getAllLocations();
            if (!pastLocations.isEmpty()) {
                for (MyLocation pLocation : pastLocations) {
                    LatLng pLatLng = new LatLng(pLocation.getLatitude(), pLocation.getLongitude());
                    MarkerOptions pOptions = new MarkerOptions().position(pLatLng).title(pLocation.getAddress()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.addMarker(pOptions);
                }
            }
            if(mLastLocation != null) {
                //Current Location
                double currentLatitude = mLastLocation.getLatitude();
                double currentLongitude = mLastLocation.getLongitude();
                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title("Current Location!");
                mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f));
            }else{
                Toast.makeText(getActivity(), "Can not Show current location", Toast.LENGTH_LONG);
            }

        }else {
            Toast.makeText(getActivity(), "Map object is null", Toast.LENGTH_LONG);
        }
    }

    private void setUpMapIfNeeded(){
        if(mMap == null){
            mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mMap =mSupportMapFragment.getMap();
            if(mMap != null){
                showLocationsInMap();
            }
        }
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
        showLocationsInMap();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this.getContext(), "Connection suspended...", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this.getContext(), "Connected changed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(this.getContext(), "Failed to connect...", Toast.LENGTH_SHORT).show();
    }
}
