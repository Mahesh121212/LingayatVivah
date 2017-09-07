package com.mahesh.com.lingayatvivah;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    String strEnteredEmail, strEnteredPassword;
    Boolean loggedIn=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadPreferences();
    // set the view now
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEnteredEmail = inputEmail.getText().toString();
                strEnteredPassword = inputPassword.getText().toString();
                VerifyUser();

                progressBar.setVisibility(View.VISIBLE);

            }
        });
    }

    public void VerifyUser() {
        String url = "https://lingayatvivah-1b59b.firebaseio.com/users/UserInfo.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("null")) {
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);
                        Iterator iter = obj.keys();
                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            JSONObject jsonObject = obj.optJSONObject(key);
                            if (jsonObject.getString("Email").equals(inputEmail.getText().toString())
                                    && jsonObject.getString("Password").equals(inputPassword.getText().toString())) {
                                AppConstant.CurrentUser=jsonObject.getString("Name");
                                AppConstant.CurrentUserMail=jsonObject.getString("Email");
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                savePreferences(jsonObject.getString("Name"),jsonObject.getString("Email"),jsonObject.getString("Password"));
                                loggedIn = true;
                                progressBar.setVisibility(View.INVISIBLE);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }
                        if (!loggedIn) {
                            Toast.makeText(LoginActivity.this, "Please Check The Entered Email or Password", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
        rQueue.add(request);
    }
    private void savePreferences(String UnameValue,String Email,String PasswordValue) {
        SharedPreferences settings = getSharedPreferences("PREFS_NAME",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("PREF_UNAME", UnameValue);
        editor.putString("PREF_UMAIL", Email);
        editor.putString("PREF_PASSWORD", PasswordValue);
        editor.putBoolean("LOGIN_STATUS", true);
        editor.commit();
    }
    private void loadPreferences() {

        SharedPreferences settings = getSharedPreferences("PREFS_NAME",
                Context.MODE_PRIVATE);
        // Get value
        String UnameValue = settings.getString("PREF_UNAME", "");
        String PasswordValue = settings.getString("PREF_PASSWORD", "");
        Boolean LoginStatus=settings.getBoolean("LOGIN_STATUS", false);
        if(LoginStatus){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }
}

