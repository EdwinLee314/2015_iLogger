package com.groupc.cse4mpc.mpcassigment;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;

/**
 * An activity representing a single Service detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ServiceListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ServiceDetailFragment}.
 */
public class ServiceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            String id = getIntent().getStringExtra(ServiceDetailFragment.ARG_ITEM_ID);
            int idNo = Integer.parseInt(id);
            Bundle arguments = new Bundle();



            arguments.putString(ServiceDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ServiceDetailFragment.ARG_ITEM_ID));

            switch(idNo) {
                case 1:
                    LocationsMapFragment locationsMapFragment = new LocationsMapFragment();
                    locationsMapFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.service_detail_container, locationsMapFragment)
                            .commit();
                    break;
                case 2:
                    WiFiScansFragment wiFiScansFragment = new WiFiScansFragment();
                    wiFiScansFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.service_detail_container,wiFiScansFragment)
                            .commit();
                    break;
                case 3:
                    BlueToothScansFragment blueToothScansFragment = new BlueToothScansFragment();
                    blueToothScansFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.service_detail_container,blueToothScansFragment)
                            .commit();
                    break;
                case 4:
                    AudioProcessFragment audioProcessFragment = new AudioProcessFragment();
                    audioProcessFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.service_detail_container,audioProcessFragment)
                            .commit();
                    break;
                case 5:
                    AudioPlayFragment audioPlayFragment = new AudioPlayFragment();
                    audioPlayFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.service_detail_container, audioPlayFragment)
                            .commit();
                    break;
                case 6:
                    PhotoTakenFragment photoTakenFragment = new PhotoTakenFragment();
                    photoTakenFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.service_detail_container, photoTakenFragment)
                            .commit();
                    break;
                case 7:
                    PhotoViewFragment photoViewFragment = new PhotoViewFragment();
                    photoViewFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.service_detail_container, photoViewFragment)
                            .commit();
                    break;
                default:
                    ServiceDetailFragment fragment = new ServiceDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.service_detail_container, fragment)
                            .commit();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ServiceListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
