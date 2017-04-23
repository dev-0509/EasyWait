package com.example.weapon_x.smartq;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
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

import static com.example.weapon_x.smartq.RegisterActivity.MyPreferences;

public class AppointmentsActivity extends AppCompatActivity implements View.OnClickListener {

    private String fetch_queues_url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/queue";
    private String manage_q_url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/queue/";
    private String signin_url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/signin";

    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ACTION = "action";

    private TextView view_qlist;

    private EditText queue_id;

    private Switch switchButton;

    private Button book_appoint;

    private FloatingActionButton search;
    private FloatingActionButton home;

    private boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        view_qlist = (TextView) findViewById(R.id.viewqueues);

        queue_id = (EditText) findViewById(R.id.qid);

        switchButton = (Switch) findViewById(R.id.status_switch);

        book_appoint = (Button) findViewById(R.id.book_appointment);

        search = (FloatingActionButton) findViewById(R.id.search_queue);
        home = (FloatingActionButton) findViewById(R.id.homeFAB);

        populateListOfQueues();

        search.setOnClickListener( this );
        book_appoint.setOnClickListener( this );
        home.setOnClickListener( this );

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String action;

                if ( isChecked ) {

                    action = "open";

                    setAppointmentStatus( action );

                } else {

                    action = "close";

                    setAppointmentStatus( action );

                }

            }

        });

    }

    private void populateListOfQueues () {

        final SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = shared.edit();

        fetchAccessToken();

        final String token = shared.getString( "token" , null );

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, fetch_queues_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            int temp = 0;

                            JSONObject object = new JSONObject( response );

                            String string = object.toString();
                            JSONObject error_status = new JSONObject( string );

                            if ( error_status.getString( "error" ).equals( "true" ) ) {

                                if ( error_status.getString( "message" ).equals( "no queues available" ) ) {

                                    view_qlist.setText( "No Queues Available" );

                                }

                            } else {

                                JSONArray queue = object.getJSONArray( "queues" );

                                for (int i = 0; i < queue.length(); i++) {
                                    JSONObject object1 = (JSONObject) queue.get(i);
                                    String id = object1.getString( "id" );

                                    if (temp == 0) {
                                        view_qlist.setText(id + "\n");
                                        ++temp;
                                    } else {
                                        view_qlist.append(id + "\n");
                                    }
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

                        Toast.makeText(AppointmentsActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }) {
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

    private void fetchAccessToken() {

        final SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = shared.edit();

        final String token = shared.getString( "token" , null );
        final String email = shared.getString( "email" , null );
        final String pass = shared.getString( "password" , null );

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, signin_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            String access_token;

                            JSONObject json = new JSONObject(response);

                            access_token = json.getString( "token" );
                            editor.putString( "token" , access_token );
                            editor.apply();

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(AppointmentsActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_EMAIL , email);
                params.put(KEY_PASSWORD , pass);
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

    private void setAppointmentStatus(final String action) {

        final SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        final String token = shared.getString( "token" , null );

        final String q_id = queue_id.getText().toString().trim();
        String copy_url = manage_q_url;

        if( TextUtils.isEmpty( q_id ) ) {

            Toast toast = Toast.makeText(AppointmentsActivity.this , "Please Specify a Queue ID !", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER , 0 , 0);
            toast.show();
            return;

        }

        copy_url = copy_url + q_id + "/appointment";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, copy_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            if ( object.getString( "accepting_appointments" ).equals( "1" )) {

                                Toast toast = Toast.makeText(AppointmentsActivity.this,
                                        "Queue " + q_id + " is open for Appointments",
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 120);
                                toast.show();

                            } else {

                                Toast toast = Toast.makeText(AppointmentsActivity.this,
                                        "Queue " + q_id + " is not accepting any Appointments",
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 120);
                                toast.show();

                            }

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText( AppointmentsActivity.this , error.toString(), Toast.LENGTH_LONG).show();
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

    private void checkIfAppointmentsOpen(final String q_id) {

        String copy_url = manage_q_url;

        copy_url = copy_url + q_id;

        if( TextUtils.isEmpty( q_id ) ) {

            Toast toast = Toast.makeText(AppointmentsActivity.this , "Please Specify a Queue ID !", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER , 0 , 0);
            toast.show();
            return;

        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, copy_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            if ( object.getString( "accepting_appointments" ).equals( "1" )) {

                                status = true;

                                switchButton.setChecked( true );

                            } else {

                                status = false;

                                switchButton.setChecked( false );

                                Toast toast = Toast.makeText(AppointmentsActivity.this,
                                        "Queue " + q_id + " is not accepting any Appointments",
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 120);
                                toast.show();

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AppointmentsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );

    }

    public void onClick(View view) {

        if( view == search ) {

            String q_id = queue_id.getText().toString().trim();

            if( TextUtils.isEmpty( q_id ) ) {

                Toast toast = Toast.makeText(AppointmentsActivity.this , "Please Specify a Queue ID !", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER , 0 , 105);
                toast.show();
                return;

            }

            checkIfAppointmentsOpen( q_id );

        }

        if ( view == book_appoint ) {

            String q_id = queue_id.getText().toString().trim();

            if( TextUtils.isEmpty( q_id ) ) {

                Toast toast = Toast.makeText(AppointmentsActivity.this , "Please Specify a Queue ID !", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER , 0 , 105);
                toast.show();
                return;

            }

            checkIfAppointmentsOpen( q_id );

            if( status ) {

                final SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = shared.edit();

                editor.putString( "queue_id" , q_id );
                editor.apply();

                Intent i = new Intent(AppointmentsActivity.this , BookingsActivity.class);
                startActivity( i );

            } else {

                Toast toast = Toast.makeText(AppointmentsActivity.this , "Appointments Closed !\n\n\t\tPlease try later", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER , 0 , 110);
                toast.show();

            }

        }

        if ( view == home ) {

            Intent i = new Intent(AppointmentsActivity.this , LaunchActivity.class);
            startActivity( i );

        }

    }

}
