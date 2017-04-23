package com.example.weapon_x.smartq;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private String add_queue_url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/queue";
    private String manage_queue_url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/queue/";

    public static final String KEY_QUEUENAME = "name";
    public static final String KEY_ACTION = "action";

    private TextView view_qlist;

    private EditText qname;
    private EditText queue_id;
    private EditText queue_name;
    private EditText queue_position;
    private EditText queue_state;

    private Button addq_button;
    private Button movenext_button;
    private Button reset_button;

    private FloatingActionButton search_id;
    private FloatingActionButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        view_qlist = (TextView) findViewById(R.id.viewqueues);

        qname = (EditText) findViewById(R.id.queuename);
        queue_id = (EditText) findViewById(R.id.qid);
        queue_name = (EditText) findViewById(R.id.name);
        queue_position = (EditText) findViewById(R.id.position);
        queue_state = (EditText) findViewById(R.id.state);

        search_id = (FloatingActionButton) findViewById(R.id.search);
        home = (FloatingActionButton) findViewById(R.id.homeFAB);

        addq_button = (Button) findViewById(R.id.buttonAddQueue);
        movenext_button = (Button) findViewById(R.id.buttonMoveNext);
        reset_button = (Button) findViewById(R.id.buttonReset);

        addq_button.setOnClickListener( this );
        search_id.setOnClickListener( this );
        movenext_button.setOnClickListener( this );
        reset_button.setOnClickListener( this );
        home.setOnClickListener( this );
    }

    private void createQueue() {

        final String queuename = qname.getText().toString().trim();

        Bundle data = getIntent().getExtras();
        final String token = data.getString( "token" );

        if( TextUtils.isEmpty( queuename ) ) {
            Toast.makeText( PublishActivity.this , "Please Specify a Queue Name !", Toast.LENGTH_LONG).show();
            return;
        }

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, add_queue_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            int temp = 0;

                            JSONObject object = new JSONObject( response );

                            JSONArray queue = object.getJSONArray("queues");

                            for(int i = 0 ; i < queue.length() ; i++) {
                                JSONObject object1 = (JSONObject) queue.get(i);
                                String id = object1.getString("id");

                                if( temp == 0 ) {
                                    view_qlist.setText(id + "\n");
                                    ++temp;
                                } else {
                                    view_qlist.append(id + "\n");
                                }
                            }

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String json = "";
                        String string = "";
                        NetworkResponse response = error.networkResponse;

                        if( response.statusCode == 401 ) {

                            json = new String( response.data );

                            try {
                                JSONObject object = new JSONObject( json );
                                string = object.toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                            Intent i = new Intent(PublishActivity.this , RegisterActivity.class);

                            Toast toast = Toast.makeText(PublishActivity.this , string, Toast.LENGTH_LONG);
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
            public Map<String , String> getHeaders() throws AuthFailureError {
                Map<String , String> headers = new HashMap<>();

                String auth = "Bearer " + token;
                headers.put("Authorization", auth);

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );

    }

    private void fetchQueueStatus() {

        String q_id = queue_id.getText().toString().trim();
        String copy_url = manage_queue_url;

        if( TextUtils.isEmpty( q_id ) ) {
            Toast.makeText( PublishActivity.this , "Please Specify a Queue ID !", Toast.LENGTH_LONG).show();
            return;
        }

        copy_url = copy_url + q_id;

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, copy_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            queue_name.setText( object.getString( "name" ) );
                            queue_position.setText( object.getString( "position" ) );

                            if ( ( object.getString( "servicestarted" ) ) == null ) {
                                queue_state.setText( "Not In Service" );
                            } else {
                                queue_state.setText( "In Service" );
                            }

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText( PublishActivity.this , error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );
    }

    private void moveNextPositionInQueue() {

        String q_id = queue_id.getText().toString().trim();
        String copy_url = manage_queue_url;
        final String action = "movenext";

        Bundle data = getIntent().getExtras();
        final String token = data.getString( "token" );

        copy_url = copy_url + q_id;

        if( TextUtils.isEmpty( q_id ) ) {
            Toast.makeText( PublishActivity.this , "Please Specify a Queue ID !", Toast.LENGTH_LONG).show();
            return;
        }

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, copy_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            queue_position.setText( object.getString( "position" ) );
                            queue_state.setText( "In Service" );

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText( PublishActivity.this , error.toString(), Toast.LENGTH_LONG).show();
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_ACTION, action);
                return params;
            }
            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                String auth = "Bearer " + token;
                headers.put("Authorization", auth);

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );
    }

    private void resetQueuePosition() {

        String q_id = queue_id.getText().toString().trim();
        String copy_url = manage_queue_url;
        final String action = "reset";

        Bundle data = getIntent().getExtras();
        final String token = data.getString( "token" );

        copy_url = copy_url + q_id;

        if( TextUtils.isEmpty( q_id ) ) {
            Toast.makeText( PublishActivity.this , "Please Specify a Queue ID !", Toast.LENGTH_LONG).show();
            return;
        }

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, copy_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            queue_position.setText( object.getString( "position" ) );
                            queue_state.setText( "Not In Service" );

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText( PublishActivity.this , error.toString() , Toast.LENGTH_LONG).show();
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_ACTION, action);
                return params;
            }
            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                String auth = "Bearer " + token;
                headers.put("Authorization", auth);

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );

    }

    @Override
    public void onClick(View view) {

        if ( view == addq_button ) {

            createQueue();

        }

        if ( view == search_id ) {

            fetchQueueStatus();

        }

        if ( view == movenext_button ) {

            moveNextPositionInQueue();

        }

        if ( view == reset_button ) {

            resetQueuePosition();

        }

        if( view == home ) {

            Intent i = new Intent(PublishActivity.this , LaunchActivity.class);
            startActivity( i );

        }
    }
}