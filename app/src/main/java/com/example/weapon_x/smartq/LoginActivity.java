package com.example.weapon_x.smartq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button;
    private EditText email;
    private EditText password;

    String usertoken;

    private String url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/signin";

    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = (Button) findViewById(R.id.buttonSignin);

        email = (EditText) findViewById(R.id.editUserEmail);
        password = (EditText) findViewById(R.id.editUserPassword);

        button.setOnClickListener( this );
    }

    private String loginUser() {

        final String useremail = email.getText().toString().trim();
        final String pass = password.getText().toString().trim();

        if( TextUtils.isEmpty( useremail ) )
        {
            // Email is empty
            Toast.makeText(this, "Missed your Email !", Toast.LENGTH_SHORT).show();
            return null;
        }

        if( TextUtils.isEmpty( pass ) )
        {
            // Password is empty
            Toast.makeText(this, "Missed your Password !", Toast.LENGTH_SHORT).show();
            return null;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONParser parser = new JSONParser();

                        try {

                            JSONObject json = new JSONObject(response);

                            usertoken = json.getString( "token" );

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_EMAIL, useremail);
                params.put(KEY_PASSWORD, pass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );

        return usertoken;
    }

    @Override
    public void onClick(View v) {

        if( v == button ) {
            usertoken = loginUser();

            Toast.makeText(LoginActivity.this, usertoken, Toast.LENGTH_LONG).show();
        }
    }
}
