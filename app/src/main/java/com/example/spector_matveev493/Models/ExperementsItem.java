package com.example.spector_matveev493.Models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.spector_matveev493.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ExperementsItem {
   public int id;
   public String date;
   public String status;
   public int statusImage;
   public String name;

   public ExperementsItem(int id, String date, String status, String name)
   {
        this.id = id;
        this.date = date;
        this.status = status;
        switch (status)
        {
            case "done": this.statusImage = R.drawable.done; break;
            case "running": this.statusImage = R.drawable.running; break;
            case "created": this.statusImage = R.drawable.created; break;
        }
        this.name = name;
   }

    public StringBuffer Date(String date)
    {
        int i = date.indexOf("T");
        String date2 = date.substring(0, i) + date.substring(i + 1);
        StringBuffer s = new StringBuffer(date2);
        s.insert(i, " ");
        i = s.indexOf(".");
        s.delete(i, s.length());
        return s;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String Time(String date)
    {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dt = LocalDateTime.parse(date, formatter);
            LocalDateTime dtn = LocalDateTime.now();

            long h = ChronoUnit.SECONDS.between(dt, dtn);
            return String.format("%02d:%02d:%02d", h / 3600, h / 60 % 60, h % 60);
        }
        catch (Exception ex)
        {
            return "0";
        }


    }
    public ExperementsItem(JSONObject obj) throws JSONException
    {
        this.id = obj.getInt("id");
        this.date = obj.getString("created_at");
        this.status = obj.getString("status");
        switch (status)
        {
            case "done": this.statusImage = R.drawable.done; break;
            case "running": this.statusImage = R.drawable.running; break;
            case "created": this.statusImage = R.drawable.created; break;
        }
        this.name = obj.getString("note");
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String toString()
    {
        if (status.equals("done"))
        {
            return String.valueOf(id) + ") " + name + " " + Date(date) + " " + status;
        }
        else
        {

            return String.valueOf(id) + ") " + name + " " + Time(String.valueOf(Date(date))) + " " + status;
        }
    }
}
