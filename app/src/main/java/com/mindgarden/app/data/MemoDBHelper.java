package com.mindgarden.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "mind_garden.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_MEMOS = "memos";
    public static final String COL_ID = "id";
    public static final String COL_SOURCE = "source";
    public static final String COL_KEYWORD = "keyword";
    public static final String COL_CONTENT = "content";
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_UPDATED_AT = "updated_at";
    public static final String COL_REFLECTION_STATUS = "reflection_status";
    public static final String COL_REFLECTION_NOTE = "reflection_note";
    public static final String COL_REFLECTED_AT = "reflected_at";

    public MemoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_MEMOS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SOURCE + " TEXT NOT NULL, "
                + COL_KEYWORD + " TEXT NOT NULL, "
                + COL_CONTENT + " TEXT NOT NULL, "
                + COL_CREATED_AT + " INTEGER NOT NULL, "
                + COL_UPDATED_AT + " INTEGER NOT NULL, "
                + COL_REFLECTION_STATUS + " TEXT, "
                + COL_REFLECTION_NOTE + " TEXT, "
                + COL_REFLECTED_AT + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Keep existing records; future schema changes must use explicit migrations.
    }
}
