package com.example.weapon_x.smartq;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.lang.String;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private String url = "http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/signup";

    public static final String MyPreferences = "MyPrefs";
    public static final String KEY_USERNAME = "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    private String usertoken;

    private Button button;
    private EditText name;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        button = (Button) findViewById(R.id.buttonRegister);

        name = (EditText) findViewById(R.id.editTextName);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);

        button.setOnClickListener( this );
    }

    public void saveToken( ) {
        Toast.makeText(RegisterActivity.this, "Save Token Called "+ usertoken,
                Toast.LENGTH_SHORT).show();

        SharedPreferences sharedpreferences = getSharedPreferences(MyPreferences, Context.MODE_APPEND);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("token", usertoken);
        editor.apply();

        String token = sharedpreferences.getString( "token" , null );

        Toast.makeText(RegisterActivity.this, "Length : " + token.length(), Toast.LENGTH_SHORT).show();
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

                            usertoken = json.getString( "token" );

                            if( TextUtils.isEmpty( usertoken ) )
                                Toast.makeText(RegisterActivity.this, "Registration Failed :(\nBe careful",
                                        Toast.LENGTH_SHORT).show();
                            else {

                                RegisterActivity.this.saveToken();

                                finish();
                            }

                       } catch ( Exception e) {

                           e.printStackTrace();

                       }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    }
}
//
//class myAsyncTask extends AsyncTask< Void, Void, String > {
//    EditText _name;
//
//    public void setOutputWindow(EditText name)
//    {
//        _name = name;
//    }
//    @Override
//    protected String doInBackground(Void... params) {
//
//        URL url;
//        String result = "";
//        HttpURLConnection urlConnection = null;
//
//        // HTTP Get
//        try {
//            url = new URL("http://ec2-34-210-16-40.us-west-2.compute.amazonaws.com:8000/api/queue/10");
//
//            urlConnection = (HttpURLConnection) url
//                    .openConnection();
//
//            InputStream in = urlConnection.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            String line;
//            StringBuilder sb= new StringBuilder();
//            while( (line = reader.readLine()) != null )
//            {
//                sb.append(line) ;
//            }
//
//            result = sb.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//        }
//        return result;
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//
//        _name.setText( result );
//        Log.i("FromOnPostExecute", result);
//    }
//}