package com.example.weapon_x.smartq;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
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

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private String url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/queue";

    public static final String KEY_QUEUENAME = "Queue_Name";

    private TextView view_qname;

    private EditText qname;

    private Button addq_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        view_qname = (TextView) findViewById(R.id.viewQName);

        qname = (EditText) findViewById(R.id.queuename);

        addq_button = (Button) findViewById(R.id.buttonAddQueue);

        addq_button.setOnClickListener( this );
    }

    private void createQueue() {

        final String queuename = qname.getText().toString().trim();

        Bundle data = getIntent().getExtras();
        final String token = data.getString( "token" );

        if( TextUtils.isEmpty( queuename ) ) {

            Toast.makeText( PublishActivity.this , "Please Specify a Queue Name !", Toast.LENGTH_LONG).show();
            return;

        }

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //JSONParser parser = new JSONParser();

                        try {

                            view_qname.setText( response );
                            //JSONObject json = new JSONObject(response);
                            //Toast.makeText(PublishActivity.this,  response, Toast.LENGTH_LONG).show();

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;

                        if( response.statusCode == 401 ) {

                            Intent i = new Intent(PublishActivity.this , RegisterActivity.class);

                            Toast toast = Toast.makeText(PublishActivity.this , "Please Register :(" , Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER , 0 , 0);
                            toast.show();

                            startActivity( i );
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_QUEUENAME, queuename);
                return params;
            }

            @Override
            public Map<String , String> getHeaders() {
                Map<String , String > headers = new HashMap<String, String>();

                String auth = "Bearers " + token;
                headers.put("Authorization" , auth );

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );

    }

    @Override
    public void onClick(View view) {

        if( view == addq_button ) {

            createQueue();

        }

    }
}
