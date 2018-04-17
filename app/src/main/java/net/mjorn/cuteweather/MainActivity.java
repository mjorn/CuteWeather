package net.mjorn.cuteweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import net.danlew.android.joda.JodaTimeAndroid;
import net.mjorn.cuteweather.utils.JsonUtils;
import net.mjorn.cuteweather.utils.LocationUtils;
import net.mjorn.cuteweather.utils.NetworkUtils;
import net.mjorn.cuteweather.utils.TranslatorUtils;

import org.json.JSONException;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        ForecastAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<String>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String XXX = ":::::::::::::::: ";

    private SharedPreferences preferences;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private ShimmerFrameLayout shimmerLayout;

    private TextView city;
    private ImageView icon;
    private TextView conditions;
    private TextView temperature;

    private RecyclerView recycler;
    private ForecastAdapter adapter;

    private static final int LOADER_ID = 27;

    private static final String EXTRA_WEATHER_NOW = "WeatherNow";
    private static final String EXTRA_FORECAST = "Forecast";
    private static final String EXTRA_LOCATION = "Location";

    public static final String EXTRA_DETAIL_DAY = "DetailDay";
    public static final String EXTRA_DETAIL_NIGHT = "DetailNight";

    private static final int REQUEST_CODE_COARSE_LOCATION = 10;
    private static final int REQUEST_CODE_FINE_LOCATION = 11;

    private String weatherNowJson;
    private String forecastJson;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing Joda Time library
        JodaTimeAndroid.init(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                latitude = JsonUtils.roundCoordinates(latitude);
                longitude = JsonUtils.roundCoordinates(longitude);

                Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.your_coordinates)+ ": " +
                                latitude + ", " +
                                longitude,
                        Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(LocationUtils.PREF_LOCATION_LAT, latitude);
                editor.putString(LocationUtils.PREF_LOCATION_LONG, longitude);

                editor.apply();

                //getWeather();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        //Displays facebook shimmering effect when loading data
        shimmerLayout = findViewById(R.id.shimmer_layout);

        city = findViewById(R.id.city);
        icon = findViewById(R.id.icon);
        conditions = findViewById(R.id.conditions);
        temperature = findViewById(R.id.temp);


        recycler = findViewById(R.id.daily_weather_list);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        getLocation();

        if (savedInstanceState != null) {
            Log.v(TAG, XXX + "LOADING FROM SAVED INSTANCE STATE");
            weatherNowJson = savedInstanceState.getString(EXTRA_WEATHER_NOW);

            if(weatherNowJson != null) {
                city.setText(JsonUtils.parseCity(weatherNowJson));
                icon.setImageResource(JsonUtils.parseIcon(weatherNowJson));
                conditions.setText(JsonUtils.parseConditions(weatherNowJson));
                temperature.setText(JsonUtils.parseTemperature(weatherNowJson));
            } else {
                getWeather();
            }

            forecastJson = savedInstanceState.getString(EXTRA_FORECAST);

            if(forecastJson != null) {
                adapter = new ForecastAdapter(
                        JsonUtils.parseDayForecast(forecastJson),
                        JsonUtils.parseNightForecast(forecastJson),
                        this);
                recycler.setAdapter(adapter);
            } else {
                getWeather();
            }

            location = savedInstanceState.getString(EXTRA_LOCATION);

            if(location != null) {
                city.setText(location);
            }

        } else {
            getWeather();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.menu_refresh:
                weatherNowJson = null;
                getWeather();
                return true;

            case R.id.menu_settings:
                Intent intent = new Intent(MainActivity.this,
                        SettingsActivity.class);
                startActivity(intent);
                return true;

        }
        return false;
    }

    public void getWeather() {
        Log.v(TAG, XXX + "GET WEATHER");

        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void getLocation() {
        /* Perform permission checks to get COARSE user LOCATION */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_COARSE_LOCATION);
                return;
            }
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location!=null) LocationUtils.updateLocation(location, this);


        /* Perform permission checks to get FINE user LOCATION */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
                return;
            }
        }
        enableLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_COARSE_LOCATION || requestCode == REQUEST_CODE_FINE_LOCATION) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableLocationUpdates();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void enableLocationUpdates() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);
        int minTime = 10;
        int minDistance = 500;
        locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        String extraDay = " ";
        try {
            extraDay = JsonUtils.parseDayForecast(forecastJson).get(position).toString();
        } catch (Exception e) {
            Log.e(TAG, "onItemClick: String extraDay:" + e);
        }

        String extraNight = " ";
        try {
             extraNight = JsonUtils.parseNightForecast(forecastJson).get(position).toString();
        } catch (Exception e) {
            Log.e(TAG, "onItemClick: String extraNight:" + e);
        }

        intent.putExtra(EXTRA_DETAIL_DAY, extraDay);
        intent.putExtra(EXTRA_DETAIL_NIGHT, extraNight);
        startActivity(intent);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                Log.d("Loader", XXX + "onStartLoading");

                if(weatherNowJson!=null) {
                    deliverResult(weatherNowJson);
                } else {
                    shimmerLayout.startShimmerAnimation();
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                Log.d("Loader ", XXX + "loadInBackground");

                //GET TODAY'S WEATHER
                URL url = NetworkUtils.buildURL(getContext());
                weatherNowJson = NetworkUtils.getJSON(url);

                //GET 5 DAY FORECAST
                URL forecastURL = NetworkUtils.buildForecastURL(getContext());
                forecastJson = NetworkUtils.getJSON(forecastURL);

                //TRANSLATE LOCATION NAME
                location = TranslatorUtils.translate(JsonUtils.parseCity(weatherNowJson));

                return weatherNowJson;
            }


        };
    }


    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        Log.d("Loader", XXX + "onLoadFinished");

        if(data!=null && !data.equals("")) {
            weatherNowJson = data;
            TransitionManager.beginDelayedTransition(shimmerLayout);

            city.setText(location);
            conditions.setText(JsonUtils.parseConditions(data));
            temperature.setText(JsonUtils.parseTemperature(data));
            icon.setImageResource(JsonUtils.parseIcon(data));
        }
        shimmerLayout.stopShimmerAnimation();

        if(forecastJson !=null) {
            adapter = new ForecastAdapter(
                    JsonUtils.parseDayForecast(forecastJson),
                    JsonUtils.parseNightForecast(forecastJson),
                    this);
            recycler.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(weatherNowJson!= null) {
            outState.putString(EXTRA_WEATHER_NOW, weatherNowJson);
        }
        if(forecastJson != null) {
            outState.putString(EXTRA_FORECAST, forecastJson);
        }
        if(location != null) {
            outState.putString(EXTRA_LOCATION, location);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key) {
            case "shimmer":
                boolean defaultValue = getResources().getBoolean(R.bool.b_shimmer);
                if(sharedPreferences.getBoolean(key, defaultValue)) {
                    shimmerLayout.startShimmerAnimation();
                }
                if(!sharedPreferences.getBoolean(key, defaultValue)) {
                    shimmerLayout.stopShimmerAnimation();
                }
                break;
            case "units":
                weatherNowJson = null;
                getWeather();
                break;
            case LocationUtils.PREF_LOCATION_LAT:
            case LocationUtils.PREF_LOCATION_LONG:
                weatherNowJson = null;
                getWeather();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
