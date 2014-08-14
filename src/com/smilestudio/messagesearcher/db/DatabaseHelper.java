package com.smilestudio.messagesearcher.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Messagers.db";
    public static final int DATABASE_VERSION = 1;

    private static final String TABLE_MESSAGER = "Messager";
    private static final String TABLE_MESSAGER_ID = "id";
    private static final String TABLE_MESSAGER_MSG_ID = "msg_id";
    private static final String TABLE_MESSAGER_CREATE_TIME = "created_at";
    private static final String TABLE_MESSAGER_TEXT = "text";
    private static final String TABLE_MESSAGER_SOURCE = "source";
    private static final String TABLE_MESSAGER_THUMB = "thumbnail";
    private static final String TABLE_MESSAGER_PICTURE = "picture";
    private static final String TABLE_MESSAGER_RETWEETED_MSG_ID = "retweeted";

    private static final String TABLE_USER = "Users";
    private static final String TABLE_USER_ID = "id";
    private static final String TABLE_USER_USER_ID = "user_id";
    private static final String TABLE_USER_NAME = "name";
    private static final String TABLE_USER_LOCATION = "location";
    private static final String TABLE_USER_THUMB = "thumbnail";
    private static final String TABLE_USER_DESC = "description";
    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGER + " ("
                    + TABLE_MESSAGER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TABLE_MESSAGER_MSG_ID + "INTEGER, "
                    + TABLE_MESSAGER_TEXT + " VARCHAR(512), "
                    + TABLE_MESSAGER_CREATE_TIME + " VARCHAR(128), "
                    + TABLE_MESSAGER_SOURCE + "VARCHAR(64), "
                    + TABLE_MESSAGER_THUMB + "VARCHAR(512), "
                    + TABLE_MESSAGER_PICTURE + "VARCHAR(512), "
                    + TABLE_MESSAGER_RETWEETED_MSG_ID + "INTEGER )");
    
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
                    + TABLE_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TABLE_USER_USER_ID + " INTEGER, "
                    + TABLE_USER_NAME + " VARCHAR(64), "
                    + TABLE_USER_LOCATION + " VARCHAR(128), "
                    + TABLE_USER_THUMB + " VARCHAR(512), "
                    + TABLE_USER_DESC + " VARCHAR(512) )");
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    public void dropTables(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE " + TABLE_MESSAGER);
            db.execSQL("DROP TABLE " + TABLE_USER);
        } catch (SQLException e) {
            Log.w(TAG, e.getMessage());
        }
    }

}
