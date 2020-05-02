package com.chakam.kampunganggrek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    public interface ItemClickListener{
        void onClick(View view, int position);
    }

    private ArrayList<Produk> dataList;
    private String KEY_TOT="TOT";
    public double tot=0;
    Context context;

    public ProdukAdapter(ArrayList<Produk> dataList, Context context)
    {
        this.context=context;
        this.dataList = dataList;
    }

    private ItemClickListener clickListener;

    @Override
    public ProdukViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProdukViewHolder holder, int position){
        holder.txtNama.setText(dataList.get(position).getNama());
        holder.txtHarga.setText("Rp. "+dataList.get(position).getHarga());

        //penggunaan library glide
        Glide.with(context) //konteks bisa didapat dari activity yang sedang berjalan
                // mengambil data dengan cara "list.get(position)" mendapatkan isi berupa objek Menu. kemudian "Menu.geturlGambar"
                .load(dataList.get(position).getImg())
                .thumbnail(0.5f) // resize gambar menjadi setengahnya
                .into(holder.icon); // mengisikan ke imageView

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class ProdukViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtNama, txtHarga;
        private ImageView icon;
        private Button btn_add;
        public ProdukViewHolder(View itemView) {
            super(itemView);
            txtNama = (TextView) itemView.findViewById(R.id.tv_produk);
            txtHarga = (TextView) itemView.findViewById(R.id.tv_harga);
            icon=(ImageView) itemView.findViewById(R.id.iv_produk);
            btn_add = (Button) itemView.findViewById(R.id.btn_add_to_cart);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            txtNama.setOnClickListener(this);
            btn_add.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) clickListener.onClick(view,
                    getAdapterPosition());
        }

    }
    public double getTot()
    {
        return  tot;
    }
    public void setTot(double tot)
    {
        this.tot=tot;
    }
}
