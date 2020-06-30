package com.chakam.kampunganggrek;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;

import java.util.ArrayList;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.PesananViewHolder>{

    private ItemClickListener clickListener;

    public interface ItemClickListener{
        boolean onClick(View view, int position);
    }

    private ArrayList<Pesanan> dataList;
    Context context;

    public PesananAdapter(ArrayList<Pesanan> dataList, Context context)
    {
        this.context=context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public PesananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_pesanan, parent, false);
        return new PesananViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PesananViewHolder holder, int position) {
        holder.txtDuedate.setText(dataList.get(position).getDue_date());
        holder.txtTotal.setText("Rp. "+dataList.get(position).getTotal());

        Log.d("status",dataList.get(position).getStatus());

        if (dataList.get(position).getStatus().equals("paid")) {
            holder.txtStatus.setText("Terbayar");
            holder.txtStatus.setTextColor(Color.GREEN);

        } else if (dataList.get(position).getStatus().equals("unpaid")) {
            holder.txtStatus.setText("Bayar");
            holder.txtStatus.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class PesananViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtDuedate, txtTotal, txtStatus;
        private NeomorphFrameLayout bayar;

        public PesananViewHolder(View itemView) {
            super(itemView);
            txtDuedate = (TextView) itemView.findViewById(R.id.tv_due_date);
            txtTotal = (TextView) itemView.findViewById(R.id.tv_total_tagihan);
            txtStatus = (TextView) itemView.findViewById(R.id.tv_status);
            bayar = (NeomorphFrameLayout) itemView.findViewById(R.id.btn_bayar);

            bayar.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) clickListener.onClick(view,
                    getAdapterPosition());
        }
    }
}
