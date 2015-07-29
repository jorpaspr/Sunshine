package com.jorpaspr.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jorpaspr.android.sunshine.app.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final int DETAIL_LOADER_ID = 0;
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

        private static final String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        };

        // these constants correspond to the projection defined above, and must change if the
        // projection changes
        private static final int COL_WEATHER_ID = 0;
        private static final int COL_WEATHER_DATE = 1;
        private static final int COL_WEATHER_DESC = 2;
        private static final int COL_WEATHER_MAX_TEMP = 3;
        private static final int COL_WEATHER_MIN_TEMP = 4;

        private String mforecast;
        private ShareActionProvider mShareActionProvider;

        public DetailFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu
            inflater.inflate(R.menu.detailfragment, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // If onLoadFinished happens before this, we can go ahead and set the share intent now.
            if (mforecast != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }

            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Intent intent = getActivity().getIntent();
            if (intent == null) return null;

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(getActivity(),
                    intent.getData(),
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null);
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.putExtra(Intent.EXTRA_TEXT, mforecast + FORECAST_SHARE_HASHTAG);
            shareIntent.setType("text/plain");
            return shareIntent;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data.moveToFirst()) {
                boolean isMetric = Utility.isMetric(getActivity());

                String date = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
                String description = data.getString(COL_WEATHER_DESC);
                String high = Utility.formatTemperature(
                        data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
                String low = Utility.formatTemperature(
                        data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

                mforecast = String.format("%s - %s - %s/%s", date, description, high, low);

                TextView textView = (TextView) getView().findViewById(R.id.detail_text);
                textView.setText(mforecast);

                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareForecastIntent());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}
