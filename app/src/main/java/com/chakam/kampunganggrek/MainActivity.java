package com.chakam.kampunganggrek;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.chakam.kampunganggrek.Util.ServerApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProdukAdapter.ItemClickListener {

    RecyclerView mRecyclerview;
    RecyclerView.Adapter mAdapter;

    RecyclerView cartRecycler;

    public static ArrayList<Produk> mItems;
    public static ArrayList<Produk> cart = new ArrayList<>();
    ProgressDialog progressDialog;
    Toast toast;
    String email,kode_kons;
    SharedPreferences sharedPreferences;

    public static final String TAG_EMAIL = "email";
    public static final String TAG_KODE_KONS = "kd_kons";
    private ProdukAdapter produkAdapter;
    public double tot = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);

        email = getIntent().getStringExtra(TAG_EMAIL);
        kode_kons = getIntent().getStringExtra(TAG_KODE_KONS);

        toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
        TextView txt_email = (TextView) findViewById(R.id.tv_nama);
        txt_email.setText(email);

        mRecyclerview = (RecyclerView) findViewById(R.id.recycler_view);
        cartRecycler = (RecyclerView) findViewById(R.id.rc_cart);
        progressDialog = new ProgressDialog(MainActivity.this);
        mItems = new ArrayList<>();

        //Membaca Semua Barang
        loadJson();

        mAdapter = new ProdukAdapter(mItems, this);
        produkAdapter = new ProdukAdapter(mItems, this);

        //GRID 2 Kolom
        Context context;
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(produkAdapter);
        produkAdapter.setClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent();
        String data;

        switch (id) {

            case R.id.action_logout:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(LoginActivity.session_status, false);
                editor.putString(TAG_EMAIL, null);
                editor.commit();

                intent.setClass(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
                return true;

            case R.id.action_call_center:
                Uri number = Uri.parse("tel:085658095085");
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
                return true;

            case R.id.action_maps:
                Uri gmmIntentUri = Uri.parse("geo:-6.928087,109.675793?q=-6.928087,109.675793");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                 return true;


            case R.id.action_sms_center:
                String no = "085658095085";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", no, null)));
                return true;


            case R.id.action_pesanan:
                Intent intent1 = new Intent(MainActivity.this, PesananActivity.class);
                intent1.putExtra(TAG_EMAIL, email);
                intent1.putExtra(TAG_KODE_KONS, kode_kons);
                startActivity(intent1);
                return true;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view, int position) {
        TextView txtJmlBeli=(TextView) findViewById(R.id.tv_jmlBeli);
        final Produk mhs = mItems.get(position);
        switch (view.getId()) {
            case R.id.tv_nama:
                Toast.makeText(this, "Ok " + mhs.getNama(), Toast.LENGTH_SHORT).show();
                return;
            case R.id.btn_add_to_cart:
                tot = tot + Double.parseDouble(String.valueOf(mhs.getHarga()));
                Toast.makeText(this, "Pesan.... " + mhs.getNama(), Toast.LENGTH_SHORT).show();
                mhs.setJmlBeli(mhs.getJmlBeli()+1);
                cart.clear();
                for(int i=0; i< mItems.size(); i++ ){
                    if(mItems.get(i).getJmlBeli()>0){
                        cart.add(mItems.get(i));
                    }
                }
                txtJmlBeli.setText("("+cart.size()+")");

              //  cartAdapter.notifyDataSetChanged();
                return;
            case R.id.card_produk:
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("gambar", mhs.getImg());
                intent.putExtra("nama", mhs.getNama());
                intent.putExtra("harga", mhs.getHarga());
                intent.putExtra("deskripsi", mhs.getDeskripsi());
                intent.putExtra("kode",mhs.getKode());
                startActivity(intent);
                break;
        }
    }

    private void loadJson() {
        progressDialog.setMessage("Mengambil Data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerApi.URL_DATA, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressDialog.cancel();

                        Log.d("volley", "response : " + response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject data = response.getJSONObject(i);
                                Produk md = new Produk();
                                md.setKode(data.getString("kd_brg"));
                                md.setNama(data.getString("nm_brg"));
                                md.setHarga(data.getInt("harga"));
                                md.setImg(data.getString("gambar"));
                                md.setDeskripsi(data.getString("deskripsi"));
                                mItems.add(md);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        produkAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel();
                        Log.d("volley", "error : " + error.getMessage());
                    }
                });

        com.chakam.kampunganggrek.Util.AppController.getInstance().addToRequestQueue(requestData);
    }

    @Override
    public void onBackPressed(){
        Context context;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Apakah Kamu Ingin Keluar? ");
        builder.setPositiveButton("Iya ", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                startActivity(intent);
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    public void cart(View view) {
        Intent intent = new Intent(MainActivity.this, CartActivity.class);
        startActivity(intent);
    }

    public void checkout(View view) {
        if(tot > 0){
            String[] kode = new String[mItems.size()];
            int[] qty = new int[mItems.size()];
            Intent checkout = new Intent(this, OngkirActivity.class);

            for (int i = 0; i < mItems.size(); i++) {
                Produk md = mItems.get(i);
                kode[i] = md.getKode();
                qty[i] = md.getJumlah();
            }

            checkout.putExtra("total", tot);
            checkout.putExtra("kode", kode);
            checkout.putExtra("qty", qty);
            checkout.putExtra(TAG_EMAIL, email);
            startActivity(checkout);
        }else{
            Toast.makeText(this, "Belanja Dulu...",Toast.LENGTH_SHORT).show();

        }
    }
}
