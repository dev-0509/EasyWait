package com.example.weapon_x.smartq;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView signin;
    private TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        signin = (TextView) findViewById(R.id.signin);
        signup = (TextView) findViewById(R.id.signup);

        signup.setOnClickListener( this );
        signin.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {

        if (v == signup) {

            Intent i = new Intent(HomeActivity.this, RegisterActivity.class);
            startActivity(i);

        }

        if (v == signin) {

            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);

        }
    }
}