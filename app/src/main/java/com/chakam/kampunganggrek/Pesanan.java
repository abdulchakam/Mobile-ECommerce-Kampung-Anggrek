package com.chakam.kampunganggrek;

public class Pesanan {
    private String invoice_id, kode_brg, nama_brg, date, due_date, status;
    private Integer harga;
    private Integer qty;
    private Integer total;

    public Pesanan(){}
    public  Pesanan (String invoice_id, String kode_brg, String nama_brg,String date,
                        String due_date, String status, Integer harga, Integer qty, Integer total) {
        this.invoice_id = invoice_id;
        this.kode_brg = kode_brg;
        this.nama_brg = nama_brg;
        this.date=date;
        this.due_date = due_date;
        this.status = status;
        this.harga =  harga;
        this.qty =  qty;
        this.total =  total;

    }


    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getKode_brg() {
        return kode_brg;
    }

    public void setKode_brg(String kode_brg) {
        this.kode_brg = kode_brg;
    }

    public String getNama_brg() {
        return nama_brg;
    }

    public void setNama_brg(String nama_brg) {
        this.nama_brg = nama_brg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getHarga() {
        return harga;
    }

    public void setHarga(Integer harga) {
        this.harga = harga;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
