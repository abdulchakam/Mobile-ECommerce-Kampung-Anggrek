package com.chakam.kampunganggrek;

public class Produk {
    private String kode;
    private String nama;
    private Integer harga;
    private Integer jumlah = 0;
    private String deskripsi;
    //private int img;
    private String img;
    private Integer jmlBeli=0;
    public Produk(String kode, String nama, String harga,String img) {
        this.kode = kode;
        this.nama = nama;
        this.harga = Integer.parseInt(harga);
        this.img=img;
    }

    public Produk() {
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public Integer getHarga() {
        return harga;
    }

    public void setHarga(Integer harga) {
        this.harga = harga;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public Integer getJumlah(){
        return jumlah;
    }
    public void setJumlah(Integer jumlah){

        this.jumlah = jumlah;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    //letak dari file gambar
    public String getImg() { return "https://kampung-anggrekid.000webhostapp.com/upload/produk/"+img;  }
    public String getImgName() { return img;  }

    public String toString(){
        return "\nkode : " + kode + " Nama : " + nama + " Harga : " + harga +"\n";
    }

    public Integer getJmlBeli() {
        return jmlBeli;
    }

    public void setJmlBeli(Integer jmlBeli) {
        this.jmlBeli = jmlBeli;
    }
}
