package com.chakam.kampunganggrek;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private CartAdapter cartAdapter;
    ArrayList<Produk> cart = MainActivity.cart;
    MainActivity mainActivity = new MainActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Button btn_back = (Button) findViewById(R.id.btn_cart_back);
        RecyclerView cartRecycler = (RecyclerView) findViewById(R.id.rc_cart);
        cartRecycler.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cart);
        cartRecycler.setAdapter(cartAdapter);
        postTotals();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void postTotals() {
        TextView txtTotal=(TextView) findViewById(R.id.tv_total);
        int tot=0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        for(int i=0; i<cart.size();i++){
            tot += (cart.get(i).getHarga() * cart.get(i).getJmlBeli());
            Log.d("vvvv", cart.get(i).getHarga()+" aaa  "+ cart.get(i).getJmlBeli());
        }
        txtTotal.setText("Rp. "+decimalFormat.format(tot));
    }
    
    
}
