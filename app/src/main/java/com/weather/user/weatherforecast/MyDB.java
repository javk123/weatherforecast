package com.weather.user.weatherforecast;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import static android.provider.BaseColumns._ID;

/**
 * Created by user on 2016/6/20.
 */
public class MyDB extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "mydb.db";
    public static final int VERSION = 1;
    private static SQLiteDatabase database;

    public MyDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String Init_Table = "CREATE TABLE " + "WEATHER" + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "data"  + " CHAR);";
        db.execSQL(Init_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        final String DROP_TABLE = "DROP TABLE IF EXISTS" + "WEATHER";
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
