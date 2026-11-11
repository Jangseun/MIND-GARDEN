package com.mindgarden.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MemoDAO {
    private final MemoDBHelper helper;

    public MemoDAO(Context context) {
        helper = new MemoDBHelper(context.getApplicationContext());
    }

    public long insert(MemoDTO memo) {
        return helper.getWritableDatabase().insert(
                MemoDBHelper.TABLE_MEMOS, null, valuesFor(memo, true));
    }

    public List<MemoDTO> selectAll() {
        List<MemoDTO> memos = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor cursor = db.query(MemoDBHelper.TABLE_MEMOS, null, null, null,
                null, null, MemoDBHelper.COL_CREATED_AT + " DESC, "
                        + MemoDBHelper.COL_ID + " DESC")) {
            while (cursor.moveToNext()) {
                memos.add(readMemo(cursor));
            }
        }
        return memos;
    }

    public MemoDTO selectById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor cursor = db.query(MemoDBHelper.TABLE_MEMOS, null,
                MemoDBHelper.COL_ID + "=?", new String[]{Long.toString(id)},
                null, null, null)) {
            return cursor.moveToFirst() ? readMemo(cursor) : null;
        }
    }

    public int update(MemoDTO memo) {
        memo.setUpdatedAt(System.currentTimeMillis());
        return helper.getWritableDatabase().update(MemoDBHelper.TABLE_MEMOS,
                valuesFor(memo, false), MemoDBHelper.COL_ID + "=?",
                new String[]{Long.toString(memo.getId())});
    }

    public int delete(long id) {
        return helper.getWritableDatabase().delete(MemoDBHelper.TABLE_MEMOS,
                MemoDBHelper.COL_ID + "=?", new String[]{Long.toString(id)});
    }

    public int countAll() {
        try (Cursor cursor = helper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + MemoDBHelper.TABLE_MEMOS, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    public int countByKeyword(String keyword) {
        try (Cursor cursor = helper.getReadableDatabase().query(
                MemoDBHelper.TABLE_MEMOS, new String[]{"COUNT(*)"},
                MemoDBHelper.COL_KEYWORD + "=?", new String[]{keyword},
                null, null, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    public int countCreatedBetween(long startInclusive, long endExclusive) {
        try (Cursor cursor = helper.getReadableDatabase().query(
                MemoDBHelper.TABLE_MEMOS, new String[]{"COUNT(*)"},
                MemoDBHelper.COL_CREATED_AT + ">=? AND "
                        + MemoDBHelper.COL_CREATED_AT + "<?",
                new String[]{Long.toString(startInclusive), Long.toString(endExclusive)},
                null, null, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    public void close() {
        helper.close();
    }

    private ContentValues valuesFor(MemoDTO memo, boolean includeCreatedAt) {
        ContentValues values = new ContentValues();
        values.put(MemoDBHelper.COL_SOURCE, memo.getSource());
        values.put(MemoDBHelper.COL_KEYWORD, memo.getKeyword());
        values.put(MemoDBHelper.COL_CONTENT, memo.getContent());
        if (includeCreatedAt) {
            values.put(MemoDBHelper.COL_CREATED_AT, memo.getCreatedAt());
        }
        values.put(MemoDBHelper.COL_UPDATED_AT, memo.getUpdatedAt());
        values.put(MemoDBHelper.COL_REFLECTION_STATUS, memo.getReflectionStatus());
        values.put(MemoDBHelper.COL_REFLECTION_NOTE, memo.getReflectionNote());
        if (memo.getReflectedAt() == null) {
            values.putNull(MemoDBHelper.COL_REFLECTED_AT);
        } else {
            values.put(MemoDBHelper.COL_REFLECTED_AT, memo.getReflectedAt());
        }
        return values;
    }

    private MemoDTO readMemo(Cursor cursor) {
        int reflectedAtIndex = cursor.getColumnIndexOrThrow(MemoDBHelper.COL_REFLECTED_AT);
        Long reflectedAt = cursor.isNull(reflectedAtIndex)
                ? null : cursor.getLong(reflectedAtIndex);
        return new MemoDTO(
                cursor.getLong(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_SOURCE)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_KEYWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_CONTENT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_CREATED_AT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_UPDATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_REFLECTION_STATUS)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_REFLECTION_NOTE)),
                reflectedAt);
    }
}
