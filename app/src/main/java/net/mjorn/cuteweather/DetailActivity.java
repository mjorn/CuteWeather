package net.mjorn.cuteweather;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import net.mjorn.cuteweather.databinding.ActivityDetailBinding;
import net.mjorn.cuteweather.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActivityDetailBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra(MainActivity.EXTRA_DETAIL_DAY);
        String jsonNight = intent.getStringExtra(MainActivity.EXTRA_DETAIL_NIGHT);

        if(jsonString!=null && !jsonString.equals("")) {
            try {

                JSONObject json = new JSONObject(jsonString);

                String weekday = JsonUtils.parseDateAsWeekday(json.getString("dt_txt"));
                bind.weekday.setText(weekday);

                String date = JsonUtils.parseDate(json.getString("dt_txt"));
                bind.date.setText(date);

                String dayTemp = JsonUtils.parseTemperature(jsonString);
                bind.dayTemp.setText(dayTemp);

                if(jsonNight!=null && !jsonNight.equals("")) {
                    String nightTemp = JsonUtils.parseMinTemp(jsonNight);
                    bind.nightTemp.setText(nightTemp);
                }

                int icon = JsonUtils.parseIcon(jsonString);
                bind.icon.setImageResource(icon);

                String pressure = JsonUtils.parsePressure(jsonString);
                bind.pressure.setText(pressure);

                String humidity = JsonUtils.parseHumidity(jsonString);
                bind.humidity.setText(humidity);

                String wind = JsonUtils.parseWind(jsonString);
                bind.wind.setText(wind);

            } catch (JSONException e) {
                Log.e(TAG, "onCreate:" + e);
            }
        }
    }
}
