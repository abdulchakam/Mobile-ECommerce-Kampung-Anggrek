package com.chakam.kampunganggrek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chakam.kampunganggrek.Util.AppController;
import com.chakam.kampunganggrek.Util.ServerApi;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_register, btn_login;
    EditText txt_email, txt_password;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public final static String TAG_EMAIL = "email";
    public final static String TAG_NAMA_KONS = "nm_kons";
    public final static String TAG_KODE_KONS = "kd_kons";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedPreferences;
    boolean session = false;
    String email, nm_kons, kd_kons;

    public static final String my_shared_preferences = "my_sahred_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView to_daftar = (TextView) findViewById(R.id.tv_ToDaftar);

        to_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            assert conMgr != null;
            if (conMgr.getActiveNetworkInfo() != null &&
                    conMgr.getActiveNetworkInfo().isAvailable() &&
                    conMgr.getActiveNetworkInfo().isConnected()) {

            } else {
                Toast.makeText(getApplicationContext(),
                        "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }

        btn_login = (Button) findViewById(R.id.button_login);
        //btn_register = (Button) findViewById(R.id.button_signupSignin);
        txt_email = (EditText) findViewById(R.id.edt_email);
        txt_password = (EditText) findViewById(R.id.edt_pass);

        // Cek Session Login jika True maka langsung buka MainActivity
        sharedPreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(session_status, false);
        email = sharedPreferences.getString(TAG_EMAIL, null);
        nm_kons = sharedPreferences.getString(TAG_NAMA_KONS, null);
        kd_kons = sharedPreferences.getString(TAG_KODE_KONS, null);

        if (session) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(TAG_EMAIL, email);
            intent.putExtra(TAG_NAMA_KONS, nm_kons);
            intent.putExtra(TAG_KODE_KONS, kd_kons);
            startActivity(intent);
            finish();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txt_email.getText().toString();
                String password = txt_password.getText().toString();

                //   Mengecek kolom yang kosong
                if (email.trim().length() > 0 &&
                        password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null &&
                            conMgr.getActiveNetworkInfo().isAvailable() &&
                            conMgr.getActiveNetworkInfo().isConnected()) {

                        cekLogin(email, password);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
        // Method Ceklogin
        private void cekLogin ( final String email, final String password){

            pDialog = new ProgressDialog(this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Logging in....");
            showDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerApi.URL_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "Login Response: " + response);
                    hideDialog();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        success = jsonObject.getInt(TAG_SUCCESS);

                        //Check For Eror node in json
                        if (success == 1) {
                            String email = jsonObject.getString(TAG_EMAIL);
                            String kd_kons = jsonObject.getString(TAG_KODE_KONS);
                            Log.e("Successfully Login!", jsonObject.toString());
                            Toast.makeText(getApplicationContext(),
                                    jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                            //Menyimpan Login Ke Session
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(session_status, true);
                            editor.putString(TAG_EMAIL, email);
                            editor.putString(TAG_KODE_KONS, kd_kons);
                            editor.commit();

                            //Memanggil MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra(TAG_EMAIL, email);
                            intent.putExtra(TAG_NAMA_KONS, nm_kons);
                            intent.putExtra(TAG_KODE_KONS, kd_kons);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                    hideDialog();
                                    pDialog.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", email);
                    params.put("password", password);

                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    //Method showDialog
    private void showDialog(){
        if(!pDialog.isShowing())
            pDialog.show();
    }

    //Method hideDialog
    private void hideDialog(){
        if(pDialog.isShowing())
            pDialog.show();
    }

}

