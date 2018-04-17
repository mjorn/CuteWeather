package net.mjorn.cuteweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import net.mjorn.cuteweather.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    public static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";

    public static final String PARAM_QUERY = "q";

    public static final String PARAM_LAT = "lat";
    public static final String PARAM_LON = "lon";

    public static final String PARAM_APPID = "APPID";
    public static final String API_KEY = "eb65f9641e4173baa11a8df019749bf6";

    public static final String PARAM_UNITS = "units";
    public static final String METRIC = "metric";
    public static final String IMPERIAL = "imperial";

    public static final String PARAM_LANG = "lang";

    public static URL buildURL(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String latitude = preferences.getString(LocationUtils.PREF_LOCATION_LAT, "53.753");
        String longitude = preferences.getString(LocationUtils.PREF_LOCATION_LONG, "37.621");

        Log.v("buildURL", "Latitude: " + latitude);
        Log.v("buildURL", "Longitude: " + longitude);

        String units = preferences.getString(context.getResources().getString(R.string.key_units), METRIC);
        String fahrenheit = context.getResources().getString(R.string.key_fahrenheit);

        String language = Locale.getDefault().getCountry();

        Uri uri = null;
        if(units.equals(fahrenheit)) {
            uri = Uri.parse(WEATHER_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(PARAM_LAT, latitude)
                    .appendQueryParameter(PARAM_LON, longitude)
                    .appendQueryParameter(PARAM_LANG, language)
                    .appendQueryParameter(PARAM_APPID, API_KEY)
                    .appendQueryParameter(PARAM_UNITS, IMPERIAL)
                    .build();
        } else {
            uri = Uri.parse(WEATHER_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(PARAM_LAT, latitude)
                    .appendQueryParameter(PARAM_LON, longitude)
                    .appendQueryParameter(PARAM_LANG, language)
                    .appendQueryParameter(PARAM_APPID, API_KEY)
                    .appendQueryParameter(PARAM_UNITS, METRIC)
                    .build();
        }

        URL resultURL = null;
        try {
            resultURL = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e("URL", "Malformed URL made from " + uri);
        }

        Log.v(TAG, "BuildURL:" + resultURL);

        return resultURL;
    }

    public static URL buildForecastURL(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String latitude = preferences.getString(LocationUtils.PREF_LOCATION_LAT, "55.755");
        String longitude = preferences.getString(LocationUtils.PREF_LOCATION_LONG, "37.617");

        String units = preferences.getString(context.getResources().getString(R.string.key_units), METRIC);
        String fahrenheit = context.getResources().getString(R.string.key_fahrenheit);

        String language = Locale.getDefault().getLanguage();

        Uri uri = null;
        if(units.equals(fahrenheit)) {
            uri = Uri.parse(FORECAST_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(PARAM_LAT, latitude)
                    .appendQueryParameter(PARAM_LON, longitude)
                    .appendQueryParameter(PARAM_LANG, language)
                    .appendQueryParameter(PARAM_APPID, API_KEY)
                    .appendQueryParameter(PARAM_UNITS, IMPERIAL)
                    .build();
        } else {
            uri = Uri.parse(FORECAST_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(PARAM_LAT, latitude)
                    .appendQueryParameter(PARAM_LON, longitude)
                    .appendQueryParameter(PARAM_LANG, language)
                    .appendQueryParameter(PARAM_APPID, API_KEY)
                    .appendQueryParameter(PARAM_UNITS, METRIC)
                    .build();
        }

        URL resultURL = null;
        try {
            resultURL = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e("URL", "Malformed URL made from " + uri);
        }

        Log.d("NetworkUtils:buildURL", "Result forecast URL:" + resultURL);

        return resultURL;
    }

    public static String getJSON(URL url) {

        String result = null;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if(scanner.hasNext()) {
                result = scanner.next();
            } else {
                Log.e("Scanner", "Scanner is empty");
            }
        } catch (IOException e) {
            Log.e(TAG , "getJson: " + e);
        } finally {
            connection.disconnect();
        }

        return result;
    }

}
