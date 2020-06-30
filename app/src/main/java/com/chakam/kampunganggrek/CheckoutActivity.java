package com.chakam.kampunganggrek;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chakam.kampunganggrek.Util.AppController;
import com.chakam.kampunganggrek.Util.ServerApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {
    private CartAdapter cartAdapter;
    ArrayList<Produk> cart = MainActivity.cart;
    ProgressDialog pd;
    double total, ongkir, totalAll, pembelian;
    DecimalFormat decimalFormat;
    TextView totalCheckout, tv_ongkir, tv_total;
    Toast toast;

    int[] qty;
    String[] kode;
    String email, tujuan;

    SharedPreferences sharedpreferences;

    public static final String TAG_EMAIL = "email";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        RecyclerView cartRecycler = (RecyclerView) findViewById(R.id.rc_cart);
        cartRecycler.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cart);
        cartRecycler.setAdapter(cartAdapter);

        sharedpreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);

        email = getIntent().getStringExtra(TAG_EMAIL);
        pembelian = getIntent().getDoubleExtra("total", 0);
        ongkir = getIntent().getDoubleExtra("ongkir",0);
        tujuan = getIntent().getStringExtra("alamat_tujuan");
        totalAll = pembelian+ongkir;

        decimalFormat = new DecimalFormat("#,##0.00");
        totalCheckout = (TextView) findViewById(R.id.totalCheckout);
        tv_total = (TextView) findViewById(R.id.total);
        tv_ongkir = (TextView) findViewById(R.id.tv_ongkir);

        pd = new ProgressDialog(CheckoutActivity.this);
        toast = Toast.makeText(getApplicationContext(),null, Toast.LENGTH_LONG);

        totalCheckout.setText("Rp. "+decimalFormat.format(total));
        tv_ongkir.setText("Rp. "+decimalFormat.format(ongkir));
        tv_total.setText("Rp. "+decimalFormat.format(totalAll));

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createJual();
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
                            for (int i = 0; i < cart.size();i++) {
                                createDetailJual(
                                        res.getString("invoices_id"),
                                        cart.get(i).getKode(),
                                        String.valueOf(cart.get(i).getJmlBeli()));
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
                params.put("pembelian", Double.toString(pembelian));
                params.put("ongkir", Double.toString(ongkir));
                params.put("total", Double.toString(totalAll));
                params.put("alamat_tujuan", tujuan);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }



    private void createDetailJual(final String invoices_id, final String kode, final String jumlah){
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
                params.put("invoices_id", invoices_id);
                params.put("kode", kode);
                params.put("jumlah", jumlah);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
