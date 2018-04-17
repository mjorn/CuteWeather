package net.mjorn.cuteweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

public class LocationUtils {

    public static final String PREF_LOCATION_LONG = "longitude";
    public static final String PREF_LOCATION_LAT = "latitude";

    public static void updateLocation(Location location, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        editor.putString(PREF_LOCATION_LAT, latitude);
        editor.putString(PREF_LOCATION_LONG, longitude);

        editor.apply();
        editor.commit();
    }
}
