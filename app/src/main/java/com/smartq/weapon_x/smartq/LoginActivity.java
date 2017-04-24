package com.smartq.weapon_x.smartq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.smartq.weapon_x.smartq.RegisterActivity.MyPreferences;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button;

    private FloatingActionButton register;
    private FloatingActionButton home;

    private EditText email;
    private EditText password;

    private String access_token;
    private String user_email;
    private String user_pass;

    private String url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/signin";

    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = (Button) findViewById(R.id.buttonSignin);

        register = (FloatingActionButton) findViewById(R.id.registerFAB);
        home = (FloatingActionButton) findViewById(R.id.homeFAB);

        email = (EditText) findViewById(R.id.editUserEmail);
        password = (EditText) findViewById(R.id.editUserPassword);

        button.setOnClickListener( this );
        home.setOnClickListener( this );
        register.setOnClickListener( this );

    }

    public void saveUserCredentials() {

        SharedPreferences sharedpreferences = getSharedPreferences(MyPreferences, Context.MODE_APPEND);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("token" , access_token);
        editor.putString("email" , user_email);
        editor.putString("password" , user_pass);
        editor.apply();

    }

    private void loginUser() {

        final String useremail = email.getText().toString().trim();
        final String pass = password.getText().toString().trim();

        if( TextUtils.isEmpty( useremail ) )
        {
            // Email is empty
            Toast.makeText(this, "Missed your Email !", Toast.LENGTH_SHORT).show();
            return;
        }

        if( TextUtils.isEmpty( pass ) )
        {
            // Password is empty
            Toast.makeText(this, "Missed your Password !", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject json = new JSONObject( response );

                            access_token = json.getString( "token" );
                            user_email = useremail;
                            user_pass = pass;

                            LoginActivity.this.saveUserCredentials();

                            Toast.makeText(LoginActivity.this, "Welcome Back :)", Toast.LENGTH_LONG).show();

                            finish();

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(LoginActivity.this, "Invalid Credentials !", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_EMAIL, useremail);
                params.put(KEY_PASSWORD, pass);
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );
    }

    @Override
    public void onClick(View view) {

        if( view == button ) {

            loginUser();

        }

        if( view == home ) {

            Intent i = new Intent(LoginActivity.this , LaunchActivity.class);
            startActivity( i );

        }

        if( view == register ) {

            Intent i = new Intent(LoginActivity.this , RegisterActivity.class);
            startActivity( i );

        }

    }

}
