package net.mjorn.cuteweather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.mjorn.cuteweather.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.WeatherViewHolder> {

    private static final String TAG = ForecastAdapter.class.getSimpleName();

    private JSONArray dayData;
    private JSONArray nightData;
    private ItemClickListener listener;

    ForecastAdapter(JSONArray dayData, JSONArray nightData, ItemClickListener listener) {
        this.dayData = dayData;
        this.nightData = nightData;
        this.listener = listener;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        try {

            JSONObject dayItem = (JSONObject) dayData.get(position);

            holder.date.setText(JsonUtils.parseDateAsWeekday(dayItem.getString("dt_txt")));
            holder.icon.setImageResource(JsonUtils.parseIcon(dayItem.toString()));
            holder.dayTemp.setText(JsonUtils.parseTemperature(dayItem.toString()));

            JSONObject nightItem = (JSONObject) nightData.get(position);
            holder.nightTemp.setText(JsonUtils.parseTemperature(nightItem.toString()));

        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e);
            holder.nightTemp.setText("?°");
        }
    }

    @Override
    public int getItemCount() {
        return dayData.length();
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView date;
        ImageView icon;
        TextView dayTemp;
        TextView nightTemp;

        WeatherViewHolder(View root) {
            super(root);

            date = root.findViewById(R.id.forecast_date);
            icon = root.findViewById(R.id.forecast_icon);
            dayTemp = root.findViewById(R.id.forecast_day_temp);
            nightTemp = root.findViewById(R.id.forecast_night_temp);

            root.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
}
