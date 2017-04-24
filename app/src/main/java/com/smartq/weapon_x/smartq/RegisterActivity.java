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
import org.json.simple.parser.JSONParser;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private String url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/signup";

    public static final String MyPreferences = "MyPrefs";
    public static final String KEY_USERNAME = "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    private String user_token;
    private String user_email;
    private String user_pass;

    private Button button;

    private FloatingActionButton home;
    private FloatingActionButton login;

    private EditText name;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        button = (Button) findViewById(R.id.buttonRegister);

        home = (FloatingActionButton) findViewById(R.id.homeFAB);
        login = (FloatingActionButton) findViewById(R.id.loginFAB);

        name = (EditText) findViewById(R.id.editTextName);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);

        button.setOnClickListener( this );
        home.setOnClickListener( this );
        login.setOnClickListener( this );
    }

    public void saveUserCredentials() {

        SharedPreferences sharedpreferences = getSharedPreferences(MyPreferences, Context.MODE_APPEND);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("token" , user_token);
        editor.putString("email" , user_email);
        editor.putString("password" , user_pass);
        editor.apply();

    }

    private void registerUser() {
        final String username = name.getText().toString().trim();
        final String useremail = email.getText().toString().trim();
        final String pass = password.getText().toString().trim();

        if( TextUtils.isEmpty( username ) ) {
            // Name is empty
            Toast.makeText(this, "I don't know you !", Toast.LENGTH_SHORT).show();
            return;
        }

        if( TextUtils.isEmpty( useremail ) ) {
            // Email is empty
            Toast.makeText(this, "Missed your Email !", Toast.LENGTH_SHORT).show();
            return;
        }

        if( TextUtils.isEmpty( pass ) ) {
            // Password is empty
            Toast.makeText(this, "Missed your Password !", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONParser parser = new JSONParser();

                        try {

                            JSONObject json = new JSONObject(response);

                            user_token = json.getString("token");
                            user_email = useremail;
                            user_pass = pass;

                            if (TextUtils.isEmpty(user_token))
                                Toast.makeText(RegisterActivity.this, "Registration Failed :(\nBe careful",
                                        Toast.LENGTH_SHORT).show();
                            else {

                                RegisterActivity.this.saveUserCredentials();

                                Toast.makeText(RegisterActivity.this, "Welcome to SmartQ !", Toast.LENGTH_LONG).show();

                                finish();
                            }
                        }

                        catch ( Exception e) {

                           e.printStackTrace();

                       }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(RegisterActivity.this, "Server is taking too long to respond\n\nPlease refresh your connection" , Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_USERNAME, username);
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

            registerUser();

        }

        if( view == home ) {

            Intent i = new Intent(RegisterActivity.this , LaunchActivity.class);
            startActivity( i );

        }

        if( view == login ) {

            Intent i = new Intent(RegisterActivity.this , LoginActivity.class);
            startActivity( i );

        }

    }

}