package com.chakam.kampunganggrek;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {
    ProgressDialog pd;
    double total, bayar, kembali = -1;
    DecimalFormat decimalFormat;
    TextView totalCheckout, kembalian;
    EditText pembayaran;
    Toast toast;

    int[] qty;
    String[] kode;
    String email;
    SharedPreferences sharedpreferences;

    public static final String TAG_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        sharedpreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);

        email = getIntent().getStringExtra(TAG_EMAIL);
        kode = getIntent().getStringArrayExtra("kode");
        qty = getIntent().getIntArrayExtra("qty");

        pd = new ProgressDialog(CheckoutActivity.this);
        toast = Toast.makeText(getApplicationContext(),null, Toast.LENGTH_LONG);
        totalCheckout = (TextView) findViewById(R.id.totalCheckout);
        total = getIntent().getDoubleExtra("total", 0);

        decimalFormat = new DecimalFormat("#,##0.00");
        totalCheckout.setText(decimalFormat.format(total));

        pembayaran = (EditText) findViewById(R.id.pembayaran);
        kembalian = (TextView) findViewById(R.id.kembalian);

        pembayaran.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(pembayaran.getText().toString().trim().length() != 0){
                    bayar = Integer.parseInt(pembayaran.getText().toString());
                    kembali = bayar - total;

                    if (kembali < 0 ){
                        kembalian.setText("0.00");
                    }else{
                        kembalian.setText(decimalFormat.format(kembali));
                    }
                }else{
                    kembalian.setText("0.00");
                }
            }
        });

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kembali < 0){
                    toast.setText("Uang Pembayaran Kurang");
                    toast.show();
                }else{
                    createJual();

                }
            }
        });
    }

    private void createJual(){
        pd.setMessage("Proses Checkout");
        pd.setCancelable(false);
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerApi.URL_JUAL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pd.cancel();
                        try {
                            JSONObject res = new JSONObject(response);
                            toast.setText(res.getString("message"));
                            toast.show();
                            for (int i = 0; i < kode.length; i++) {
                                if (qty[i] != 0) {
                                    String skode = kode[i];
                                    int sqty = qty[i];
                                    createDetailJual(skode, sqty);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.cancel();
                toast.setText("Gagal Checkout");
                toast.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                //Posting parameters to nota
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", email);
                params.put("total", Double.toString(total));
                params.put("pembayaran",
                        Double.toString(bayar));
                params.put("kembalian",
                        Double.toString(kembali));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    private void createDetailJual(final String kode, final int qty){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerApi.URL_DETAIL_JUAL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //Posting parameters to detail nota
                Map<String, String> params = new HashMap<String, String>();

                params.put("kode", kode);
                params.put("jumlah", Integer.toString(qty));

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}

