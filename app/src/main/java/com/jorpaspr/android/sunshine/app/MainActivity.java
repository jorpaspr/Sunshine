package com.jorpaspr.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jorpaspr.android.sunshine.app.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity
        implements ForecastFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DETAIL_FRAGMENT";

    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocation = Utility.getPreferredLocation(this);

        // The detail container view will be present only in the large-screen layouts
        // (res/layout-sw600dp). If this view is present, then the activity should be
        // in two-pane mode.
        mTwoPane = (findViewById(R.id.weather_detail_container) != null);
        // In two-pane mode, show the detail view in this activity by
        // adding or replacing the detail fragment using a
        // fragment transaction.
        if (mTwoPane && savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            getSupportActionBar().setElevation(0);
        }

        ((ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast))
                .setmUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            mLocation = location;

            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_forecast);
            if (ff != null) ff.onLocationChanged();

            DetailFragment df = (DetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(DETAILFRAGMENT_TAG);
            if (df != null) df.onLocationChanged(location);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_map:
                openPreferredLocationInMap();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(this);
        Uri geolocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location).build();
        Intent intentLocation = new Intent(Intent.ACTION_VIEW, geolocation);
        if (intentLocation.resolveActivity(getPackageManager()) != null) {
            startActivity(intentLocation);
        } else {
            Log.d(LOG_TAG, "Cannot resolve the intent with the location " + location);
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, dateUri);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(dateUri);
            startActivity(intent);
        }
    }
}
