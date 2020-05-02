package com.chakam.kampunganggrek;

import androidx.appcompat.app.AppCompatActivity;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chakam.kampunganggrek.Util.AppController;
import com.chakam.kampunganggrek.Util.ServerApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.chakam.kampunganggrek.LoginActivity.TAG_EMAIL;
import static com.chakam.kampunganggrek.LoginActivity.session_status;

public class RegisterActivity extends AppCompatActivity {
    ConnectivityManager conMgr;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "json_obj_req";
    SharedPreferences sharedpreferences;

    public static final String my_shared_preferences = "my_shared_preferences";
    private EditText txt_nama, txt_alamat, txt_kota, txt_kodepos, txt_phone, txt_email, txt_pass;
    private Button btn_register;
    private TextView login;
    private int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    &&
                    conMgr.getActiveNetworkInfo().isAvailable()
                    &&
                    conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }

        sharedpreferences = getSharedPreferences(my_shared_preferences,Context.MODE_PRIVATE);

        txt_nama = (EditText) findViewById(R.id.edt_nama);
        txt_alamat = (EditText) findViewById(R.id.edt_alamat);
        txt_kota = (EditText) findViewById(R.id.edt_kota);
        txt_kodepos = (EditText) findViewById(R.id.edt_kodepos);
        txt_phone = (EditText) findViewById(R.id.edt_kota);
        txt_email = (EditText) findViewById(R.id.edt_email);
        txt_pass = (EditText) findViewById(R.id.edt_pass);
        btn_register = (Button) findViewById(R.id.button_register);
        login = (TextView) findViewById(R.id.tv_ToLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pDialog = new ProgressDialog(RegisterActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage("Tunggu Sebentar ...");
                pDialog.show();
                StringRequest strReq = new StringRequest(Request.Method.POST, ServerApi.URL_REGISTRASI, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", "register Response: " + response);
                        pDialog.cancel();
                        TextView nama = (TextView) findViewById(R.id.edt_nama);
                        TextView alamat = (TextView) findViewById(R.id.edt_alamat);
                        TextView kota = (TextView) findViewById(R.id.edt_kota);
                        TextView kodepos = (TextView) findViewById(R.id.edt_kodepos);
                        TextView phone = (TextView) findViewById(R.id.edt_phone);
                        TextView regis_email = (TextView) findViewById(R.id.edt_email);
                        TextView password = (TextView) findViewById(R.id.edt_pass);

                        nama.setText(null);
                        alamat.setText(null);
                        kota.setText(null);
                        kodepos.setText(null);
                        phone.setText(null);
                        regis_email.setText(null);
                        password.setText(null);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);
                            // Check for error node in json
                            if (success == 1) {
                                String email = jObj.getString(TAG_EMAIL);
                                Log.e("Berhasil Mendaftar!", jObj.toString());
                                Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                // menyimpan login ke session
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putBoolean(session_status,true);
                                editor.putString(TAG_EMAIL, email);
                                editor.apply();

                                // Memanggil main activity
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra(TAG_EMAIL, email);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Register Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        pDialog.cancel();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("nama", txt_nama.getText().toString());
                        params.put("alamat", txt_alamat.getText().toString());
                        params.put("kota", txt_kota.getText().toString());
                        params.put("kodepos", txt_kodepos.getText().toString());
                        params.put("phone", txt_phone.getText().toString());
                        params.put("email", txt_email.getText().toString());
                        params.put("password", txt_pass.getText().toString());
                        return params;
                    }
                };
                AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
            }
        });
    }
}
