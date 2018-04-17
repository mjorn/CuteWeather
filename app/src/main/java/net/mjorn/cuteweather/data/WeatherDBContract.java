package net.mjorn.cuteweather.data;

import android.provider.BaseColumns;

public class WeatherDBContract {

    public static class Forecast implements BaseColumns {
        public static final String TABLE_NAME = "forecast";

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_DAY_TEMP = "day_temp";
    }

}
