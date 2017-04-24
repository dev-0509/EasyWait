package com.smartq.weapon_x.smartq;

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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class BookingsActivity extends AppCompatActivity implements View.OnClickListener {

    private String manage_q_url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/queue/";

    public static final String KEY_ACTION = "action";
    public static final String KEY_REFERENCE = "reference";
    public static final String KEY_POSITION = "position";

    private TextView view_appoints;

    private EditText queue_reference;
    private EditText cancel_appoint;

    private Button book;
    private Button cancel;
    private Button view_appointments;

    private RadioButton show_queue_id;

    private FloatingActionButton home;

    private String position;
    private String parameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        String string = "QUEUE : ";

        view_appoints = (TextView) findViewById(R.id.view_appointments);

        queue_reference = (EditText) findViewById(R.id.q_reference);
        cancel_appoint = (EditText) findViewById(R.id.cancel_appointment);

        book = (Button) findViewById(R.id.buttonBook);
        cancel = (Button) findViewById(R.id.buttonCancel);
        view_appointments = (Button) findViewById(R.id.fetch_appointments);

        show_queue_id = (RadioButton) findViewById(R.id.radioButton);

        home = (FloatingActionButton) findViewById(R.id.homeFAB);

        final SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        if ( ! (shared.getString( "queue_id" , null ).equals( null ) ) ) {

            show_queue_id.setText( string );
            show_queue_id.append( shared.getString( "queue_id" , null ) );

        }

        home.setOnClickListener( this );
        book.setOnClickListener( this );
        cancel.setOnClickListener( this );
        view_appointments.setOnClickListener( this );
        show_queue_id.setOnClickListener( this );

    }

    private void appointmentHandler(final String action , final String queue_id) {

        final SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        final String token = shared.getString( "token" , null );

        String copy_url = manage_q_url;

        if ( action.equals( "book" ) ) {

            parameter = queue_reference.getText().toString().trim();

        } else {

            parameter = cancel_appoint.getText().toString().trim();

        }

        if( TextUtils.isEmpty( parameter ) ) {

            if ( action.equals( "book" ) ) {

                Toast toast = Toast.makeText(BookingsActivity.this, "Please Specify a Reference to your Appointment!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, -290);
                toast.show();
                return;

            } else {

                Toast toast = Toast.makeText(BookingsActivity.this, "Please Specify the Position to Cancel !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, -200);
                toast.show();
                return;

            }

        }

        copy_url = copy_url + queue_id + "/appointment";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, copy_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            if ( action.equals( "book" ) ) {

                                JSONObject object = new JSONObject( response );

                                position = object.getString("position");

                                view_appoints.setText( "Queue : " );
                                view_appoints.append( queue_id );
                                view_appoints.append( "\t\tPosition : " );
                                view_appoints.append( position );

                                Toast toast = Toast.makeText( BookingsActivity.this ,
                                        "Position " + position + " booked in Queue " + queue_id ,
                                        Toast.LENGTH_LONG );
                                toast.setGravity( Gravity.CENTER , 0 , 0 );
                                toast.show();

                            } else {

                                Toast toast = Toast.makeText( BookingsActivity.this ,
                                        "Appointment for Position " + parameter + " cancelled in Queue " + queue_id ,
                                        Toast.LENGTH_LONG );
                                toast.setGravity( Gravity.CENTER , 0 , 0 );
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

                        Toast.makeText( BookingsActivity.this , "Server is taking too long to respond\n\nPlease refresh your connection" , Toast.LENGTH_LONG).show();

                    }

                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_ACTION , action);

                if ( action.equals( "book" ) ) {

                    params.put(KEY_REFERENCE, parameter);

                } else {

                    params.put(KEY_POSITION, parameter);

                }

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

    private void fetchAllAppointments(String queue_id) {

        final SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        final String token = shared.getString( "token" , null );

        String copy_url = manage_q_url;

        copy_url = copy_url + queue_id + "/appointment";

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, copy_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            int temp = 0;

                            JSONObject object = new JSONObject( response );

                            JSONArray appointments_list = object.getJSONArray( "appointments" );

                            for( int i = 0 ; i < appointments_list.length() ; i++ ) {

                                JSONObject object1 = (JSONObject) appointments_list.get(i);

                                String appoint_name = object1.getString( "reference" );
                                String position = object1.getString( "position" );

                                if( temp == 0 ) {

                                    view_appoints.setText( appoint_name + "\t\t-\t\tPosition : " + position + "\n" );
                                    ++temp;

                                } else {

                                    view_appoints.append( appoint_name + "\t\t-\t\tPosition : " + position + "\n" );

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

                        Toast.makeText( BookingsActivity.this , "Server is taking too long to respond\n\nPlease refresh your connection" , Toast.LENGTH_LONG).show();

                    }

                }) {
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

    public void onClick(View view) {

        if ( view == home ) {

            Intent i = new Intent(BookingsActivity.this , LaunchActivity.class);
            startActivity( i );

        }

        if ( view == book ) {

            if ( show_queue_id.isChecked() ) {

                SharedPreferences shared = getSharedPreferences( "MyPrefs" , Context.MODE_PRIVATE );

                String queue_id = shared.getString( "queue_id" , null );

                appointmentHandler( "book" , queue_id );

            } else {

                Toast toast = Toast.makeText( BookingsActivity.this , "Please mark your Queue !", Toast.LENGTH_SHORT );
                toast.setGravity( Gravity.START , 0 , -330 );
                toast.show();

            }

        }

        if ( view == cancel ) {

            if ( show_queue_id.isChecked() ) {

                SharedPreferences shared = getSharedPreferences( "MyPrefs" , Context.MODE_PRIVATE );

                String queue_id = shared.getString( "queue_id" , null );

                appointmentHandler( "cancel" , queue_id );

            } else {

                Toast toast = Toast.makeText( BookingsActivity.this , "Please mark your Queue !", Toast.LENGTH_SHORT );
                toast.setGravity( Gravity.START , 0 , -330 );
                toast.show();

            }

        }

        if ( view == view_appointments ) {

            if ( show_queue_id.isChecked() ) {

                SharedPreferences shared = getSharedPreferences( "MyPrefs" , Context.MODE_PRIVATE );

                String queue_id = shared.getString( "queue_id" , null );

                fetchAllAppointments( queue_id );

            } else {

                Toast toast = Toast.makeText( BookingsActivity.this , "Please mark your Queue !", Toast.LENGTH_SHORT );
                toast.setGravity( Gravity.START , 0 , -330 );
                toast.show();

            }

        }

        if ( view == show_queue_id ) {

            show_queue_id.setChecked( true );

        }

    }

}