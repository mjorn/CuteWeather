package net.mjorn.cuteweather.utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class TranslatorUtils {

    private static final String TAG = TranslatorUtils.class.getSimpleName();

    /* This class is for translating location name
       provided by the weather API into Russian */

    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";

    private static final String PARAM_KEY = "key";
    private static final String KEY = "trnsl.1.1.20180416T155113Z.59e31b8fcbe143e4.56873aa316347067ff28184992fee252a8f944a7";

    private static final String PARAM_LANG = "lang";
    private static final String LANG_EN = "en";

    private static final String PARAM_TEXT = "text";

    public static String translate(String text) {
        String lang = Locale.getDefault().getCountry().toLowerCase();

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_KEY, KEY)
                .appendQueryParameter(PARAM_LANG, LANG_EN + "-" + lang)
                .appendQueryParameter(PARAM_TEXT, text)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "translate: " + text);
        }

        Log.d(TAG, "translate: " + "URL: " + url);

        if(url!=null) {
            try {

                JSONObject json = new JSONObject(NetworkUtils.getJSON(url));
                JSONArray array = json.getJSONArray("text");
                text = array.getString(0);

            } catch (Exception e) {
                Log.e(TAG, "translate: " + e);
            }
        }

        //if translate fails, just return original text
        return text;
    }
}
