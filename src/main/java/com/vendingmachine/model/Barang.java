package com.vendingmachine.model;

import java.io.Serializable;

/**
 * Kelas model untuk merepresentasikan produk dalam vending machine.
 * Menyimpan informasi lengkap tentang produk termasuk identitas,
 * nama, harga, stok, dan path gambar.
 * 
 * Kelas ini mengimplementasikan Serializable untuk mendukung
 * penyimpanan data persisten ke file.
 * 
 * @author Tim Pengembang Vending Machine
 * @version 2.0
 * @since 2024
 */
public class Barang implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID unik untuk mengidentifikasi barang
     */
    private String idBarang;

    /**
     * Nama barang yang akan ditampilkan
     */
    private String namaBarang;

    /**
     * Harga barang dalam Rupiah
     */
    private double hargaBarang;

    /**
     * Jumlah stok barang yang tersedia saat ini
     */
    private int stokSekarang;

    /**
     * Path relatif atau absolut ke file gambar produk
     */
    private String pathGambar;

    /**
     * Constructor untuk membuat objek Barang baru.
     *
     * @param idBarang ID unik barang
     * @param namaBarang Nama barang
     * @param hargaBarang Harga barang dalam Rupiah
     * @param stokAwal Jumlah stok awal
     * @param pathGambar Path ke file gambar produk
     */
    public Barang(String idBarang, String namaBarang, double hargaBarang, int stokAwal, String pathGambar) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
        this.stokSekarang = stokAwal;
        this.pathGambar = pathGambar;
    }

    /**
     * Mendapatkan ID barang.
     *
     * @return ID barang
     */
    public String getIdBarang() {
        return idBarang;
    }

    /**
     * Mengatur ID barang.
     *
     * @param idBarang ID barang baru
     */
    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    /**
     * Mendapatkan nama barang.
     *
     * @return Nama barang
     */
    public String getNamaBarang() {
        return namaBarang;
    }

    /**
     * Mengatur nama barang.
     *
     * @param namaBarang Nama barang baru
     */
    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    /**
     * Mendapatkan harga barang.
     *
     * @return Harga barang dalam Rupiah
     */
    public double getHargaBarang() {
        return hargaBarang;
    }

    /**
     * Mengatur harga barang.
     *
     * @param hargaBarang Harga barang baru dalam Rupiah
     */
    public void setHargaBarang(double hargaBarang) {
        this.hargaBarang = hargaBarang;
    }

    /**
     * Mendapatkan jumlah stok saat ini.
     *
     * @return Jumlah stok tersedia
     */
    public int getStokSekarang() {
        return stokSekarang;
    }

    /**
     * Mengatur jumlah stok.
     *
     * @param stokSekarang Jumlah stok baru
     */
    public void setStokSekarang(int stokSekarang) {
        this.stokSekarang = stokSekarang;
    }

    /**
     * Mendapatkan path gambar produk.
     *
     * @return Path ke file gambar
     */
    public String getPathGambar() {
        return pathGambar;
    }

    /**
     * Mengatur path gambar produk.
     *
     * @param pathGambar Path baru ke file gambar
     */
    public void setPathGambar(String pathGambar) {
        this.pathGambar = pathGambar;
    }

    /**
     * Mengurangi stok barang dengan jumlah tertentu.
     * Memastikan stok tidak menjadi negatif.
     *
     * @param jumlah Jumlah yang akan dikurangi dari stok
     * @throws IllegalArgumentException jika jumlah lebih besar dari stok tersedia
     */
    public void kurangiStok(int jumlah) {
        if (jumlah > stokSekarang) {
            throw new IllegalArgumentException("Jumlah yang diminta melebihi stok tersedia!");
        }
        if (jumlah < 0) {
            throw new IllegalArgumentException("Jumlah tidak boleh negatif!");
        }
        this.stokSekarang -= jumlah;
    }

    /**
     * Mengembalikan representasi String dari objek Barang.
     *
     * @return String berisi informasi barang
     */
    @Override
    public String toString() {
        return String.format("Barang[ID=%s, Nama=%s, Harga=Rp %.2f, Stok=%d]",
                           idBarang, namaBarang, hargaBarang, stokSekarang);
    }
}