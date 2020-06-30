package com.chakam.kampunganggrek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chakam.kampunganggrek.Util.AppController;
import com.chakam.kampunganggrek.Util.DownloadTask;
import com.chakam.kampunganggrek.Util.ServerApi;
import com.kosalgeek.android.photoutil.GalleryPhoto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.chakam.kampunganggrek.Util.ServerApi.DOWNLOAD_NOTA;


public class StatusActivity extends AppCompatActivity {

    TextView status, tanggal, tv_email, total, tv_nota;
    ImageView imgStatus, gambar;
    Button btnsimpan, btncetak, btngallery;
    ProgressDialog pd;
    GalleryPhoto mGalery;
    DecimalFormat decimalFormat;
    int PICK_IMAGE_REQUEST = 111;
    public static final String TAG_EMAIL = "email";
    Bitmap bitmap;
    String date, email, kode_kons, nota;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        kode_kons = sharedpreferences.getString("kd_kons", null);

        Log.d("kode",kode_kons);


        status = (TextView) findViewById(R.id.text_status);
        tanggal = (TextView) findViewById(R.id.tanggal);
        tv_nota = (TextView) findViewById(R.id.no_nota);
        tv_email = (TextView) findViewById(R.id.user);
        email=getIntent().getStringExtra(TAG_EMAIL);

        nota = getIntent().getStringExtra("no_nota");
        email=getIntent().getStringExtra(TAG_EMAIL);

        tv_nota.setText(nota);
        tv_email.setText(email);
        total = (TextView) findViewById(R.id.total_biaya);
        imgStatus = (ImageView) findViewById(R.id.img_status);
        gambar = (ImageView) findViewById(R.id.inp_gambar);
        btngallery = (Button) findViewById(R.id.btn_gallery);
        btncetak = (Button) findViewById(R.id.btn_cetak);
        btnsimpan = (Button) findViewById(R.id.btn_simpan);
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        decimalFormat = new DecimalFormat("#,##0.00");
        pd = new ProgressDialog(StatusActivity.this);
        mGalery = new GalleryPhoto(getApplicationContext());
        loadJson();

        btngallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
//                startActivityForResult(mGalery.openGalleryIntent(), TAG_GALLERY);
            }
        });

        btncetak.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String url = DOWNLOAD_NOTA+tv_nota.getText().toString();
                new DownloadTask(StatusActivity.this, url, tv_nota.getText().toString());
            }
        });

        btnsimpan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                simpanData();
            }
        });

    }


    private void loadJson() {
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        StringRequest sendData = new
                StringRequest(Request.Method.POST, ServerApi.URL_DASHBOARD_JUAL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                pd.cancel();
                                try {
                                    JSONObject data = new JSONObject(response);

                                    tanggal.setText(data.getString("date"));
//                                    tv_nota.setText(data.getString("id"));
                                    total.setText("Rp. " + decimalFormat.format(data.getInt("total_biaya")));

                                    if (data.getString("status").equals("paid")) {
                                        status.setText("Sudah Dibayar");

                                        status.setTextColor(Color.GREEN);

                                        imgStatus.setImageResource(R.drawable.berhasil);

                                        btngallery.setVisibility(View.GONE);

                                        btnsimpan.setVisibility(View.GONE);

                                        findViewById(R.id.rekening).setVisibility(View.GONE);
                                    } else if (data.getString("status").equals("unpaid")) {
                                        status.setText("Belum Dibayar");
                                        status.setTextColor(Color.RED);
                                        imgStatus.setImageResource(R.drawable.gagal);

                                        btncetak.setVisibility(View.GONE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError
                                                                error) {
                                pd.cancel();
                                Log.d("volley", "error : " +
                                        error.getMessage());
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<>();
                        map.put("kd_kons", kode_kons);
                        map.put("no_nota", nota);
                        return map;
                    }
                };
        AppController.getInstance().addToRequestQueue(sendData, "json_obj_req");
    }


    private void simpanData() {
        pd.setMessage("Mengirim Data");
        pd.setCancelable(false);
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        StringRequest sendData = new
                StringRequest(Request.Method.POST, ServerApi.URL_JUAL2,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                pd.cancel();
                                try {
                                    JSONObject res = new
                                            JSONObject(response);
                                    Toast.makeText(StatusActivity.this, "pesan : " +

                                            res.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(getApplicationContext(), PesananActivity.class);
                                intent.putExtra(TAG_EMAIL, email);
                                startActivity(intent);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError
                                                                error) {
                                pd.cancel();
                                Toast.makeText(StatusActivity.this,
                                        "pesan : Gagal Kirim Data", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<>();
                        map.put("no_nota", nota);
                        map.put("gambar", imageString);
                        return map;
                    }
                };

        AppController.getInstance().addToRequestQueue(sendData);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                gambar.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}