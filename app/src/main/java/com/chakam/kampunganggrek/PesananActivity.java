package com.chakam.kampunganggrek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.chakam.kampunganggrek.Util.AppController;
import com.chakam.kampunganggrek.Util.ServerApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PesananActivity extends AppCompatActivity implements PesananAdapter.ItemClickListener {

    RecyclerView mRecyclerview;
    RecyclerView.Adapter mAdapter;

    public static ArrayList<Pesanan> mItems;
    ProgressDialog pd;

    private PesananAdapter pesananAdapter;

    String kode_kons, email;

    SharedPreferences sharedPreferences;

    public static final String TAG_EMAIL = "email";
    public static final String TAG_KODE_KONS = "kd_kons";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);



        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        kode_kons = sharedpreferences.getString("kd_kons", null);
        email = getIntent().getStringExtra(TAG_EMAIL);


        Log.d("kode",kode_kons);

        mRecyclerview = (RecyclerView) findViewById(R.id.rc_pesanan);
        pd = new ProgressDialog(PesananActivity.this);
        mItems = new ArrayList<>();

        //Membaca Semua Barang
        loadJson();

        mAdapter = new PesananAdapter(mItems, this);
        pesananAdapter = new PesananAdapter(mItems, this);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.setAdapter(pesananAdapter);
        pesananAdapter.setClickListener(this);
    }



    private void loadJson() {
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        StringRequest sendData = new StringRequest(Request.Method.POST, ServerApi.URL_PESANAN,
                new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loadData();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.cancel();
                                Log.d("volley", "error: " +
                                        error.getMessage());
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("kd_kons", kode_kons);
                        Log.d("ini Kode Konsumen", String.valueOf(kode_kons));
                        return map;
                    }
                };
        AppController.getInstance().addToRequestQueue(sendData, "json_obj_req");
    }



    private void loadData() {
        pd.setMessage("Mengambil Data");
        pd.setCancelable(false);
        pd.show();

        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerApi.URL_PESANAN, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        pd.cancel();
                        Log.d("volley", "response : " + response.toString());

                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject data = response.getJSONObject(i);
                                Log.e("tagconvertstr", "["+response+"]");
                                Pesanan md = new Pesanan();
                                md.setInvoice_id(data.getString("id"));
                                md.setDue_date(data.getString("date"));
                                md.setStatus(data.getString("status"));
                                md.setTotal(data.getInt("total_biaya"));
                                mItems.add(md);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        pesananAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        Log.d("volley", "error Load data : " + error.getMessage());
                    }
                });

        com.chakam.kampunganggrek.Util.AppController.getInstance().addToRequestQueue(requestData);
    }

    @Override
    public boolean onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.btn_bayar:
                Intent intent1 = new Intent(PesananActivity.this, StatusActivity.class);
                intent1.putExtra(TAG_EMAIL, email);
                intent1.putExtra(TAG_KODE_KONS, kode_kons);
                intent1.putExtra("no_nota", mItems.get(position).getInvoice_id());
                startActivity(intent1);

                return true;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
