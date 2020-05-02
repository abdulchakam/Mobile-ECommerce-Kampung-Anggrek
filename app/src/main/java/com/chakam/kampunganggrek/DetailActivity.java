package com.chakam.kampunganggrek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;

public class DetailActivity extends AppCompatActivity {

    String gambar, harga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);



        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(0);

        Intent intent = getIntent();
        this.harga = intent.getStringExtra("harga");
        this.gambar = intent.getStringExtra("gambar");

        TextView nama_detail = (TextView) findViewById(R.id.tv_nama_detail);
        TextView deskripsi_detail = (TextView) findViewById(R.id.tv_deskripsi_detail);
        TextView harga_detail = (TextView) findViewById(R.id.tv_harga_detail);
        ImageView gambar_detail = (ImageView) findViewById(R.id.iv_produk_detail);

        nama_detail.setText(getIntent().getStringExtra("nama"));
        deskripsi_detail.setText(getIntent().getStringExtra("deskripsi"));
        harga_detail.setText("Rp. "+nf.format(Double.parseDouble(harga)));

        Glide.with(DetailActivity.this)
                .load(gambar)
                .thumbnail(0.5f)
                .into(gambar_detail);

        ImageView back =(ImageView) findViewById(R.id.btn_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
