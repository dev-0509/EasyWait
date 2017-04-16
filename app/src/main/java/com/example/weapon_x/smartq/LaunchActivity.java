package com.example.weapon_x.smartq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import java.util.HashMap;
import java.util.Map;

import static com.example.weapon_x.smartq.RegisterActivity.MyPreferences;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {

    private String url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/queue/";

    public static final String KEY_QUEUE = "queue";

    private EditText queueid;
    private EditText label;

    private Button viewButton;
    private Button publishButton;

    private String queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        queueid = (EditText) findViewById(R.id.queueid);
        label = (EditText) findViewById(R.id.label);

        viewButton = (Button) findViewById(R.id.buttonView);
        publishButton = (Button) findViewById(R.id.buttonPublish);

        viewButton.setOnClickListener(this);

        publishButton.setOnClickListener( this );
    }

    private void getQueue() {

        final String id = queueid.getText().toString().trim();

        url = url + id;

        if (TextUtils.isEmpty( id )) {
            // Queue ID is empty
            Toast.makeText(this, "Please specify a Queue ID !", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            label.setText( response );

                        } catch (Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LaunchActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );
    }

    private void checkIfRegistered() {

        SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        String token = shared.getString( "token" , null );

        if( token == null ) {

            // Prompt for Registration
            Intent i = new Intent(LaunchActivity.this , RegisterActivity.class);

            startActivity( i );

        }

        else {

            Intent i = new Intent(LaunchActivity.this , PublishActivity.class);

            i.putExtra( "token" , token );
            startActivity( i );
        }

    }

    @Override
    public void onClick(View view) {
        if ( view == viewButton ) {
            getQueue();
        }

        if( view == publishButton ) {
            checkIfRegistered();
        }
    }
}
