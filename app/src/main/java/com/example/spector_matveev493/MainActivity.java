package com.example.spector_matveev493;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.spector_matveev493.Models.ApiHelper;
import com.example.spector_matveev493.Models.ChemElement;
import com.example.spector_matveev493.Models.DB;
import com.example.spector_matveev493.Models.SpecLine;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    Button ok;
    Context ctx;
    Dialog dialog;
    Switch sw;
    SeekBar sb;
    Activity context;
    static Spinner sp;
    SpectraView sv;
    ArrayAdapter<ChemElement> adp;
    Button b;
    public static DB base;

    public void zoomIn(View v)
    {
        float wlen_center = (sv.wlen_max + sv.wlen_min)/2.0f;
        float wlen_dist = wlen_center - sv.wlen_min;
        float zoom_percent = 0.1f;
        sv.wlen_min += wlen_dist * zoom_percent;
        sv.wlen_max -= wlen_dist * zoom_percent;
        sv.have_background = false;
        sv.invalidate();
    }
    public void zoomOut(View v)
    {
        float wlen_center = (sv.wlen_max + sv.wlen_min)/2.0f;
        float wlen_dist = wlen_center - sv.wlen_min;
        float zoom_percent = 0.1f;
        sv.wlen_min -= wlen_dist * zoom_percent;
        sv.wlen_max += wlen_dist * zoom_percent;
        sv.have_background = false;
        sv.invalidate();
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        base = new DB(this, "setting.db", null, 1);

        try {

        }
        catch (Exception ex)
        {

        }

        sv = findViewById(R.id.spectraView);
        context = this;
        ctx = this;

        sp = findViewById(R.id.spinner);
        adp = new ArrayAdapter<ChemElement>(this, android.R.layout.simple_list_item_1);


        ApiHelper req = new ApiHelper(this)
        {
            @Override
            public void on_ready(String res)
            {
                try {
                    JSONArray arr = new JSONArray(res);
                    for (int i = 0; i < arr.length(); i++)
                    {
                        adp.add(new ChemElement(arr.getJSONObject(i)));
                    }
                    sp.setAdapter(adp);
                    int item = Integer.parseInt(base.getLines());
                    sp.setSelection(item);
                }catch (Exception ex){}
            }
        };
        req.send("http://spectra.spbcoit.ru/lab/spectra/api/rpc/get_elements", "{}");



        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ChemElement el = (ChemElement) sp.getSelectedItem();

                ApiHelper req = new ApiHelper(context)
                {
                    @Override
                    public void on_ready(String res)
                    {
                        try {
                            JSONArray arr = new JSONArray(res);
                            for (int i = 0; i < arr.length(); i++)
                            {
                                sv.lines.add(new SpecLine(arr.getJSONObject(i)));
                            }
                            sv.invalidate();
                        }catch (Exception ex){}
                    }
                };
                sv.lines.clear();
                req.send("http://spectra.spbcoit.ru/lab/spectra/api/rpc/get_lines", "{\"atomic_num\": " +
                        String.valueOf(el.atomic_num) +"}");
                sv.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sv.ctx = this;
        sv.setWillNotDraw(false);

        sv.wlen_max = Float.valueOf(base.getWlen_max());
        sv.wlen_min = Float.valueOf(base.getWlen_min());
        sv.bg_lum = Float.valueOf(base.getBg_lum());
        sv.invalidate();


    }
    public void ApplicationSettings(View v)
    {
        Intent i = new Intent(this, Setting.class);
        startActivity(i);
    }
    public void DisplaySettings(View v)
    {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.display_setting);
        dialog.show();
        ok = dialog.findViewById(R.id.buttonOk);
        sw = dialog.findViewById(R.id.switchX);
        sb = dialog.findViewById(R.id.seekBar);
        try {
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.base.setSettingView(String.valueOf(sv.wlen_max), String.valueOf(sv.wlen_min), String.valueOf(sv.bg_lum), String.valueOf(sp.getSelectedItemPosition()));
                    dialog.dismiss();
                }
            });

            sb.setProgress((int) (sv.bg_lum * 100));
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    sv.bg_lum = (float) i / 100;
                    sv.invalidate();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            sw.setChecked(SpectraView.axisX);
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SpectraView.axisX = b;
                    sv.invalidate();
                }
            });

        }
        catch (Exception ex)
        {}



    }

}