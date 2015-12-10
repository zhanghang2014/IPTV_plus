package com.bigheart.byrtv.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by BigHeart on 15/12/8.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final int DbVersion = 1; //当前版本号
    public static String DbName = "iptv_plus.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SqlColumn.TABLE_NAME + " (" +
                    SqlColumn.ID + " INTEGER PRIMARY KEY," +
                    SqlColumn.SAYING + TEXT_TYPE + " )";


    public DbHelper(Context context) {
        super(context, DbName, null, DbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
