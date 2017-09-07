package com.mahesh.com.lingayatvivah;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputName,inputMobileNo,inputConfirmPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    String email,password,name,mobileno,confirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Firebase.setAndroidContext(this);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputName = (EditText) findViewById(R.id.input_name);
        inputMobileNo = (EditText) findViewById(R.id.input_mobile_number);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirmPassword = (EditText) findViewById(R.id.reenterpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    email= inputEmail.getText().toString().trim();
                    password = inputPassword.getText().toString().trim();
                    name = inputName.getText().toString().trim();
                    mobileno = inputMobileNo.getText().toString().trim();
                    confirmPassword = inputConfirmPassword.getText().toString().trim();


                    if (name.equals("")) {
                        inputName.setError("Enter valid User Name");
                    } else if (email.equals("")) {
                        inputName.setError("Enter valid Email Id");
                    } else if (password.equals("")) {
                        inputPassword.setError("can't be blank");
                    } else if (password.length() < 5) {
                        inputPassword.setError("at least 5 characters long");
                    } else if (!isValidMobile(mobileno)) {
                        inputMobileNo.setError("Enter valid Mobile Number");
                    } else if ((!confirmPassword.equals(password))) {
                        inputConfirmPassword.setError("Enter same password");
                    } else {
                        getUID();

                        progressBar.setVisibility(View.VISIBLE);

                        String url = "https://lingayatvivah-1b59b.firebaseio.com/users/UserInfo.json";

                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Firebase reference = new Firebase("https://lingayatvivah-1b59b.firebaseio.com/users/UserInfo");
                                if (s.equals("null")) {
                                    reference.child("U"+AppConstant.UsetId.toString()).child("Name").setValue(name);
                                    reference.child("U"+AppConstant.UsetId.toString()).child("Password").setValue(password);
                                    reference.child("U"+AppConstant.UsetId.toString()).child("Email").setValue(email);
                                    reference.child("U"+AppConstant.UsetId.toString()).child("Mobile").setValue(mobileno);
                                    reference.child("U"+AppConstant.UsetId.toString()).child("AddUserFlag").setValue("U"+AppConstant.UsetId.toString()+"TRUE");

                                    Toast.makeText(SignupActivity.this, "registration successful", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        JSONObject obj = new JSONObject(s);
                                        if (!obj.has("U"+AppConstant.UsetId.toString())&& AppConstant.AddUser) {
                                            reference.child("U"+AppConstant.UsetId.toString()).child("Name").setValue(name);
                                            reference.child("U"+AppConstant.UsetId.toString()).child("Password").setValue(password);
                                            reference.child("U"+AppConstant.UsetId.toString()).child("Email").setValue(email);
                                            reference.child("U"+AppConstant.UsetId.toString()).child("Mobile").setValue(mobileno);
                                            reference.child("U"+AppConstant.UsetId.toString()).child("AddUserFlag").setValue("U"+AppConstant.UsetId.toString()+"TRUE");
                                            Toast.makeText(SignupActivity.this, "registration successful", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                System.out.println("" + volleyError);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                        RequestQueue rQueue = Volley.newRequestQueue(SignupActivity.this);
                        rQueue.add(request);

                    }

                }catch (Exception e){
                    Log.d("",e.toString());
                }
            }
        });


    }


    private boolean isValidMobile(String phone2)
    {
        boolean check;
        if(phone2.length() < 6 || phone2.length() > 13)
        {
            check = false;
        }
        else
        {
            check = true;
        }
        return check;
    }

    public void getUID() {
        final int[] p = {0};
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
                            if(jsonObject.getString("Name").equals(name)
                                    &&jsonObject.getString("Email").equals(email)){
                                Toast.makeText(SignupActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                                AppConstant.AddUser=false;
                            }else{
                                p[0] = p[0] + 1;
                                AppConstant.AddUser=true;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                AppConstant.UsetId = p[0] +1;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(SignupActivity.this);
        rQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}