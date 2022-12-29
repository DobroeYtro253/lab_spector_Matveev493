package com.example.spector_matveev493;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.example.spector_matveev493.Models.ApiHelper;
import com.example.spector_matveev493.Models.SpecLine;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SpectraView extends SurfaceView {
    ArrayList<SpecLine> lines = new ArrayList<SpecLine>();

    float bg_lum = 0.25f;

    boolean have_background = false;

    float wlen_min = 380.0f;
    float wlen_max = 780.0f;


    public static boolean axisX = true;
    Activity ctx;

    JSONArray arr;

    Paint p;

    public SpectraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
    }

    void download_background(final SpectraView me, int steps)
    {
        ApiHelper req = new ApiHelper(ctx)
        {
            @Override
            public void on_ready(String res)
            {
                try {
                    arr = new JSONArray(res);
                } catch (Exception ex){}
                have_background = true;
                me.invalidate();
            }
        };

        JSONObject obj = new JSONObject();
        try {
            obj.put("nm_from", wlen_min);
            obj.put("nm_to", wlen_max);
            obj.put("steps", steps);
        }catch (Exception ex){}

        req.send("http://spectra.spbcoit.ru/lab/spectra/api/rpc/nm_to_rgb_range", obj.toString());
    }

    float lerp(float a, float b, float t)
    {
        return a + (b - a) * t;
    }
    float unlerp(float x, float x0, float x1)
    {
        return (x - x0)/(x1 - x0);
    }

    float map(float x, float x0, float x1, float a, float b)
    {
        float t = unlerp(x, x0, x1);
        return lerp(a, b, t);
    }

    float last_x = 0.0f;
    int img_w;
    boolean moving = false;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                last_x = event.getX();
                moving = true;
                return true;

            case MotionEvent.ACTION_UP:
                moving = false;
                have_background = false;
                MainActivity.base.setSettingView(String.valueOf(wlen_max), String.valueOf(wlen_min), String.valueOf(bg_lum), String.valueOf(MainActivity.sp.getSelectedItemPosition()));
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                float new_x = event.getX();
                float delta_x = new_x - last_x;
                float delta_nm = wlen_max - wlen_min;
                float nm_per_pixel = delta_nm /img_w;
                wlen_min -= delta_x * nm_per_pixel;
                wlen_max -= delta_x * nm_per_pixel;
                last_x = event.getX();
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        img_w = w;

        canvas.drawColor(Color.BLACK);

        if (have_background == false)
            download_background(this, w);
        else
        {
            if(moving == false)
            {
                for (int i = 0; i < arr.length(); i++)
                {
                    try {
                        JSONObject obj = arr.getJSONObject(i);
                        int r = (int) (obj.getDouble("red") * bg_lum * 255.0);
                        int g = (int) (obj.getDouble("green") * bg_lum * 255.0);
                        int b = (int) (obj.getDouble("blue") * bg_lum * 255.0);
                        p.setARGB(255, r, g, b);
                    } catch (Exception ex){}
                    canvas.drawLine(i, 0, i, h, p);
                }
            }
        }

        for (int i = 0; i < lines.size(); i++)
        {
            SpecLine sl = lines.get(i);
            float x = map(sl.wavelength, wlen_min, wlen_max, 0, w - 1);
            sl.setPaintColor(p);
            canvas.drawLine(x, 0, x, h, p);
        }

        Integer[] nm = {380, 440, 490, 510, 580, 645, 780};

        if(axisX == true)
        {
            for (int i = 0; i < nm.length; i++)
            {
                float x = map(nm[i], wlen_min, wlen_max, 0, w - 1);
                // p.setARGB(255,88, 157, 82);
                p.setARGB(255,255, 255, 255);
                canvas.drawLine(x, 0, x, 50, p);
                canvas.drawText(String.valueOf(nm[i]), x, 60, p);
            }
        }


    }
}
