package com.example.weapon_x.smartq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {

    private String view_queue_url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/queue/";

    private EditText queueid;
    private EditText label;

    private Button viewButton;
    private Button proceedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        queueid = (EditText) findViewById(R.id.queueid);
        label = (EditText) findViewById(R.id.label);

        viewButton = (Button) findViewById(R.id.buttonView);
        proceedButton = (Button) findViewById(R.id.buttonProceed);

        viewButton.setOnClickListener( this );
        proceedButton.setOnClickListener( this );
    }

    private void getQueue() {

        final String id = queueid.getText().toString().trim();
        String copy_url = view_queue_url;

        copy_url = copy_url + id;

        if (TextUtils.isEmpty( id )) {
            // Queue ID is empty
            Toast.makeText(this, "Please specify a Queue ID !", Toast.LENGTH_SHORT).show();
            return;

        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, copy_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            label.setText( "Current Position : ");
                            label.append( object.getString( "position" ) );

                        } catch (Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(LaunchActivity.this , "Server is taking too long to respond\n\nPlease refresh your connection" , Toast.LENGTH_LONG).show();

                    }

                });

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );
    }

    @Override
    public void onClick(View view) {

        if ( view == viewButton ) {

            getQueue();

        }

        if ( view == proceedButton ) {

            Intent i = new Intent( LaunchActivity.this , ChoiceActivity.class );
            startActivity( i );

        }
    }
}