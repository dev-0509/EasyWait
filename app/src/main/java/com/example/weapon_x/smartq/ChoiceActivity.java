package com.example.weapon_x.smartq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class ChoiceActivity extends AppCompatActivity implements View.OnClickListener {

    private String signin_url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/signin";

    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    String access_token;

    private Button signup;
    private Button signin;
    private Button publishButton;
    private Button appointmentsButton;

    private ImageView register;
    private ImageView login;

    private FloatingActionButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        signup = (Button) findViewById(R.id.newUser);
        signin = (Button) findViewById(R.id.buttonSignin);
        publishButton = (Button) findViewById(R.id.buttonPublish);
        appointmentsButton = (Button) findViewById(R.id.buttonAppointments);

        register = (ImageView) findViewById(R.id.registerImage);
        login = (ImageView) findViewById(R.id.loginImage);

        home = (FloatingActionButton) findViewById(R.id.homeFAB);

        signup.setOnClickListener( this );
        signin.setOnClickListener( this );
        register.setOnClickListener( this );
        login.setOnClickListener( this );
        publishButton.setOnClickListener( this );
        home.setOnClickListener( this );
        appointmentsButton.setOnClickListener( this );

    }

    private void checkIfRegistered(final View view) {

        final SharedPreferences shared = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = shared.edit();

        final String token = shared.getString( "token" , null );
        final String email = shared.getString( "email" , null );
        final String pass = shared.getString( "password" , null );

        if( token == null ) {

            // Prompt for Registration
            Toast toast = Toast.makeText(ChoiceActivity.this , "Please Login or Register :(", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER , 0 , 0);
            toast.show();

            Intent i = new Intent(ChoiceActivity.this , LoginActivity.class);

            startActivity( i );

        }

        else {
            // Check for Authorized User (Sign-in)
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, signin_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject json = new JSONObject(response);

                                access_token = json.getString( "token" );
                                editor.putString( "token" , access_token );
                                editor.apply();

                                if ( view == publishButton ) {

                                    String token = shared.getString( "token" , null );

                                    Intent i = new Intent(ChoiceActivity.this , PublishActivity.class);

                                    i.putExtra( "token" , token );
                                    startActivity( i );

                                } else if ( view == appointmentsButton ) {

                                    Intent i = new Intent(ChoiceActivity.this, AppointmentsActivity.class);
                                    startActivity(i);

                                }

                            } catch ( Exception e) {

                                e.printStackTrace();

                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Intent i = new Intent(ChoiceActivity.this , LoginActivity.class);

                            Toast toast = Toast.makeText(ChoiceActivity.this , "Please Login or Register :(", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER , 0 , 0);
                            toast.show();

                            startActivity( i );

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
    }

    @Override
    public void onClick ( View view ) {

        if ( view == signup || view == register ) {

            Intent i = new Intent(ChoiceActivity.this , RegisterActivity.class);
            startActivity( i );

        }

        if ( view == signin || view == login ) {

            Intent i = new Intent(ChoiceActivity.this , LoginActivity.class);
            startActivity( i );

        }

        if( view == publishButton ) {

            checkIfRegistered( view );

        }

        if( view == home ) {

            Intent i = new Intent(ChoiceActivity.this , LaunchActivity.class);
            startActivity( i );

        }

        if ( view == appointmentsButton ) {

            checkIfRegistered( view );

        }
    }
}
