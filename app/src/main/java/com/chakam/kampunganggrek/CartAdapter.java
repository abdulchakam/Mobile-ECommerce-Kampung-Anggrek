package com.chakam.kampunganggrek;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ProdukViewHolder> {
    private static ArrayList<Produk> cartlist;

    public CartAdapter(ArrayList<Produk> datalist){
        this.cartlist = datalist;
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_cart , parent, false);
        return new ProdukViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        holder.txtNama.setText(cartlist.get(position).getNama());
        holder.txtSubtotal.setText("Rp. "+decimalFormat.format((cartlist.get(position).getHarga() * cartlist.get(position).getJmlBeli())));
        holder.txtHarga.setText("Rp. "+decimalFormat.format(cartlist.get(position).getHarga()));
        holder.txtQty.setText(String.valueOf(cartlist.get(position).getJmlBeli()));
    }

    @Override
    public int getItemCount() {
        return cartlist.size();
    }

    public class ProdukViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNama, txtHarga, txtSubtotal, txtQty;
        public ProdukViewHolder(View itemView) {
            super(itemView);
            txtNama = (TextView) itemView.findViewById(R.id.tv_cart_nama);
            txtHarga = (TextView) itemView.findViewById(R.id.tv_cart_harga);
            txtSubtotal = (TextView) itemView.findViewById(R.id.tv_cart_jumlah);
            txtQty = (TextView) itemView.findViewById(R.id.tv_qty);
        }
    }
}
