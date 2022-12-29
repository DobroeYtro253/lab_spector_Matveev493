package com.example.spector_matveev493.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Settings (Api TEXT, wlen_max TEXT, wlen_min TEXT, bg_lum TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE displaySettings (wlen_max TEXT, wlen_min TEXT, bg_lum TEXT, lines TEXT)";
        db.execSQL(sql);
    }
    public void setSetting(String api)
    {
        String sql = "DELETE FROM Settings";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        sql = "INSERT INTO Settings (Api) VALUES('" + api + "');";
        db = getWritableDatabase();
        db.execSQL(sql);
    }
    public void setSettingView(String wlen_max,String wlen_min,String bg_lum, String lines)
    {
        String sql = "DELETE FROM displaySettings";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        sql = "INSERT INTO displaySettings (wlen_max, wlen_min, bg_lum, lines) VALUES('" + wlen_max + "', '" + wlen_min + "', '" + bg_lum + "', '" + lines + "');";
        db = getWritableDatabase();
        db.execSQL(sql);
    }
    public String getApi()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT Api FROM Settings;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst() == true) return cur.getString(0);
        return "0";
    }
    public String getWlen_max()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT wlen_max FROM displaySettings;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst() == true) return cur.getString(0);
        return "0";
    }
    public String getWlen_min()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT wlen_min FROM displaySettings;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst() == true) return cur.getString(0);
        return "0";
    }
    public String getBg_lum()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT bg_lum FROM displaySettings;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst() == true) return cur.getString(0);
        return "0";
    }
    public String getLines()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT lines FROM displaySettings;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst() == true) return cur.getString(0);
        return "0";
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
