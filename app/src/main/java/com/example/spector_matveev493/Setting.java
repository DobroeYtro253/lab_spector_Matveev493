package com.example.spector_matveev493;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Setting extends AppCompatActivity {

    EditText api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        api = findViewById(R.id.editTextAPI);
        api.setText(MainActivity.base.getApi());
    }
    public void Save(View v)
    {
        MainActivity.base.setSetting(api.getText().toString());
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void Cancel(View v)
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}