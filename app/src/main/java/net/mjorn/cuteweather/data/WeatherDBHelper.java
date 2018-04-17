package net.mjorn.cuteweather.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.mjorn.cuteweather.data.WeatherDBContract.Forecast;

public class WeatherDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 5;

    private static final String INTEGER = " INTEGER";
    private static final String TEXT = " TEXT";
    private static final String NOT_NULL = " NOT NULL";

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /* create table for the five day forecast */
        final String SQL_CREATE_FORECAST_TABLE =
                        "CREATE TABLE " + Forecast.TABLE_NAME + " (" +
                        Forecast._ID +               INTEGER + "PRIMARY KEY AUTOINCREMENT, " +
                        Forecast.COLUMN_DATE +       TEXT + NOT_NULL + ", " +
                        Forecast.COLUMN_ICON +       TEXT + NOT_NULL + ", " +
                        Forecast.COLUMN_DAY_TEMP +   TEXT + NOT_NULL + ", " +
                        "UNIQUE (" + Forecast.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_FORECAST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Forecast.TABLE_NAME);
        onCreate(db);
    }
}
