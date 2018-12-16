package com.example.luksza.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nickEditText = findViewById(R.id.nickEditText);
        ipEditText = findViewById(R.id.ipEditText);





    }

    public static String IP="ip";
    public static String NICK="nick";


    public void onClick(View v) {

        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        intent.putExtra(IP, ipEditText.getText().toString());
        intent.putExtra(NICK, nickEditText.getText().toString());
        startActivity(intent);
    }

    EditText nickEditText;
    EditText ipEditText;




}
