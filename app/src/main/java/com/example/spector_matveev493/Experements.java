package com.example.spector_matveev493;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spector_matveev493.Models.ApiHelper;
import com.example.spector_matveev493.Models.ExperementsItem;
import com.example.spector_matveev493.Models.SpecLine;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Experements extends AppCompatActivity {

    ArrayList<Integer> exRun = new ArrayList<Integer>();
    ExperimentsAdapter ei;
    String[] experiments;
    String[] status;
    String[] tags;
    TextView tag;
    Activity context;
    ListView lv;
    Integer tagId;

    private void timerTick() {
        this.runOnUiThread(doTask);
    }

    private Runnable doTask = new Runnable() {
        public void run() {
            if (exRun.size() != 0)
            {
                for (int i = 0; i < exRun.size(); i++)
                {
                    int finalI = exRun.get(i);
                    int I = i;
                    ApiHelper req = new ApiHelper(context)
                    {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void on_ready(String res)
                        {
                            try {

                                JSONArray arr = new JSONArray(res);

                                    experiments[finalI] = new ExperementsItem(arr.getJSONObject(finalI)).toString();
                                    status[finalI] = arr.getJSONObject(finalI).getString("status");
                                    if (status[finalI].equals("done"))
                                    {exRun.remove(I);} else {}

                                lv.setAdapter(new ExperimentsAdapter(context, android.R.layout.activity_list_item, experiments));
                                ei.notifyDataSetChanged();
                                lv.invalidate();
                            }catch (Exception ex)
                            {}
                        }
                    };
                    req.send("http://labs-api.spbcoit.ru:80/lab/spectra/api/rpc/get_experiments", "{\"tagname\": \"" +
                            tags[tagId] +"\"}");

                }

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experements);

        Timer myTimer;
        myTimer = new Timer();

        myTimer.schedule(new TimerTask() {
            public void run() {
                timerTick();
            }
        }, 0, 1000);



        lv = findViewById(R.id.experimentsList);
        context = this;
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent in = new Intent(context, MainActivity.class);
                    startActivity(in);
                }
            });


        tag = findViewById(R.id.textViewTag);
        ApiHelper req = new ApiHelper(context)
        {
            @Override
            public void on_ready(String res)
            {
                try {
                    JSONArray arr = new JSONArray(res);
                    tags = new String[arr.length()];
                    for (int i = 0; i < arr.length(); i++)
                    {
                        tags[i] = arr.getString(i);
                    }
                }catch (Exception ex){}
            }
        };
        req.send("http://labs-api.spbcoit.ru:80/lab/spectra/api/rpc/get_tags", "{}");

    }

    public void SelectTag(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select tag")
                .setItems(tags, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        tag.setText("Tag: " + tags[which]);
                        tagId = which;
                        exRun.clear();
                        ApiHelper req = new ApiHelper(context)
                        {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void on_ready(String res)
                            {
                                try {

                                    JSONArray arr = new JSONArray(res);
                                    experiments = new String[arr.length()];
                                    status = new String[arr.length()];
                                    for (int i = 0; i < arr.length(); i++)
                                    {
                                        experiments[i] = new ExperementsItem(arr.getJSONObject(i)).toString();
                                        status[i] = arr.getJSONObject(i).getString("status");
                                        if (status[i].equals("done"))
                                        {} else {exRun.add(i);}
                                    }

                                    lv.setAdapter(new ExperimentsAdapter(context, android.R.layout.activity_list_item, experiments));
                                    ei.notifyDataSetChanged();
                                    lv.invalidate();
                                }catch (Exception ex)
                                {}
                            }
                        };
                        req.send("http://labs-api.spbcoit.ru:80/lab/spectra/api/rpc/get_experiments", "{\"tagname\": \"" +
                                tags[which] +"\"}");

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private class ExperimentsAdapter extends ArrayAdapter<String> {

        ExperimentsAdapter(Context context, int textViewResourceId,
                      String[] objects) {
            super(context, textViewResourceId, objects);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(android.R.layout.activity_list_item, parent, false);
            TextView label = (TextView) row.findViewById(android.R.id.text1);
            label.setText(experiments[position]);
            ImageView iconImageView = (ImageView) row.findViewById(android.R.id.icon);
            switch (status[position])
            {
                case "done":  iconImageView.setImageResource(R.drawable.done); break;
                case "running": iconImageView.setImageResource(R.drawable.running); break;
                case "created": iconImageView.setImageResource(R.drawable.created); break;
            }
            return row;
        }
    }

}