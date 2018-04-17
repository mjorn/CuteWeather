package net.mjorn.cuteweather.utils;

import android.content.Context;
import android.util.Log;

import net.mjorn.cuteweather.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JsonUtils {

    private static final String TAG = "JSON";

    public static String parseCity(String data) {
        try {

            JSONObject allData = new JSONObject(data);
            String location = allData.getString("name");
            return location;

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return "";
    }

    public static String parseTemperature(String data) {
        try {

            JSONObject allData = new JSONObject(data);
            JSONObject mainData = allData.getJSONObject("main");
            String temp = mainData.getString("temp");
            if(temp.contains(".")) temp = round(temp) + "°";
            return temp;

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return 0 + "°";
    }

    public static String parseMinTemp(String data) {
        try {

            JSONObject allData = new JSONObject(data);
            JSONObject mainData = allData.getJSONObject("main");
            String temp = mainData.getString("temp_min");
            if(temp.contains(".")) temp = round(temp) + "°";
            return temp;

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return "?°";
    }

    public static String parseConditions(String data) {
        try {

            JSONObject allData = new JSONObject(data);
            JSONArray weatherArray = (JSONArray) allData.get("weather");
            JSONObject weatherMain = weatherArray.getJSONObject(0);
            return weatherMain.getString("description");

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return "";
    }

    public static int parseIcon(String data) {
        try {

            JSONObject allData = new JSONObject(data);
            JSONArray weatherArray = (JSONArray) allData.get("weather");
            JSONObject weatherMain = weatherArray.getJSONObject(0);
            int id = weatherMain.getInt("id");

            switch(id) {
                case 800: //clear
                    return R.drawable.ic_sun;
            }

            id = Integer.parseInt(String.valueOf(id).substring(0,1));

            switch(id) {
                case 2:
                    return R.drawable.ic_thunderstorm;
                case 5:
                    return R.drawable.ic_rain;
                case 6:
                    return R.drawable.ic_snow;
                case 7: //fog
                case 8:
                    return R.drawable.ic_cloud;
                default:
                    return R.drawable.ic_sun;
            }

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return R.drawable.ic_sun;
    }

    public static JSONArray parseDayForecast(String forecast) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONArray result = new JSONArray();
            JSONObject allData = new JSONObject(forecast);
            JSONArray list = allData.getJSONArray("list");

            //only get temperature at 12:00 each day
            for(int i=0; i<list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String dateString = item.getString("dt_txt");
                Date date = sdf.parse(dateString);
                DateTime jodaDate = new DateTime(date);

                if(jodaDate.hourOfDay().get()==12) {
                    result.put(item);
                }
            }
            return result;

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        } catch (ParseException e) {
            Log.e("JodaDate", e.toString());
        }
        return null;
    }

    public static JSONArray parseNightForecast(String forecast) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONArray result = new JSONArray();
            JSONObject allData = new JSONObject(forecast);
            JSONArray list = allData.getJSONArray("list");
            DateTime now = new DateTime();

            //only get temperature at 21:00 each day
            for(int i=0; i<list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String dateString = item.getString("dt_txt");
                Date date = sdf.parse(dateString);
                DateTime jodaDate = new DateTime(date);

                if(jodaDate.hourOfDay().get()==0) {
                    result.put(item);
                }
            }
            return result;

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        } catch (ParseException e) {
            Log.e("JodaDate", e.toString());
        }
        return null;
    }

    public static String parseDateAsWeekday(String dateString) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(dateString);
            DateTime jodaDate = new DateTime(date);
            String weekday = jodaDate.dayOfWeek().getAsText(Locale.getDefault());
            //Make first letter uppercase
            weekday = weekday.substring(0,1).toUpperCase() + weekday.substring(1, weekday.length());
            return weekday;

        } catch (ParseException e) {
            Log.e("JodaDate", e.toString());
        }
        return "";
    }

    public static String parseDate(String dateString) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Locale locale = Locale.getDefault();
            Date date = sdf.parse(dateString);
            DateTime jodaDate = new DateTime(date);
            String month = jodaDate.monthOfYear().getAsText(locale);
            String day = jodaDate.dayOfMonth().getAsShortText();
            String result;

            //if RUSSIAN
            boolean russian = locale.getCountry().equals("RU");
            if(russian) result = day + " " + month;
            else result = month + " " + day;
            return result;

        } catch (ParseException e) {
            Log.e("JodaDate", e.toString());
        }
        return "";
    }

    public static String parsePressure(String data) {
        try {

            JSONObject allData = new JSONObject(data);
            JSONObject mainData = allData.getJSONObject("main");
            String pressure = mainData.getString("pressure");
            if(pressure.contains(".")) pressure = round(pressure);
            return pressure;

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return "";
    }

    public static String parseHumidity(String data) {
        try {

            JSONObject allData = new JSONObject(data);
            JSONObject mainData = allData.getJSONObject("main");
            return mainData.getString("humidity");

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return "";
    }

    public static String parseWind(String data) {
        try {

            JSONObject allData = new JSONObject(data);
            JSONObject windData = allData.getJSONObject("wind");
            String wind = windData.getString("speed");
            return wind;

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return "";
    }

    public static String round(String s) {
        return s.substring(0, s.indexOf("."));
    }

    public static String roundCoordinates(String s) {
        try {
            return s.substring(0, s.indexOf(".")+3);
        } catch(IndexOutOfBoundsException e) {
            Log.e(TAG, "roundCoordinates: " + s);
        }
        return s;
    }
}
