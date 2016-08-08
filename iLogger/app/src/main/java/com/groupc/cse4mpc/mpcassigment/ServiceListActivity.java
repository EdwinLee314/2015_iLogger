package com.groupc.cse4mpc.mpcassigment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


/**
 * An activity representing a list of Services. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ServiceDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ServiceListFragment} and the item details
 * (if present) is a {@link ServiceDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ServiceListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ServiceListActivity extends FragmentActivity
        implements ServiceListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        if (findViewById(R.id.service_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ServiceListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.service_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link ServiceListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        int idNo = Integer.parseInt(id);
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ServiceDetailFragment.ARG_ITEM_ID, id);
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

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ServiceDetailActivity.class);
            detailIntent.putExtra(ServiceDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
