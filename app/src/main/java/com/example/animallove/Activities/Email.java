package com.example.animallove.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.animallove.R;

public class Email extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        String email = getIntent().getStringExtra("email");

        Uri uri = Uri.parse("mailto:"+email);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(it);
    }
}

