package com.vendingmachine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Kelas model utama untuk sistem vending machine.
 * Mengelola logika bisnis untuk operasi produk dan transaksi,
 * termasuk manajemen inventori dan pemrosesan pembelian.
 * 
 * Fitur yang disediakan:
 * - Manajemen daftar produk (CRUD operations)
 * - Validasi dan pemrosesan transaksi pembelian
 * - Manajemen stok produk
 * - Riwayat transaksi
 * - Integrasi dengan sistem persistensi data
 * 
 * @author Tim Pengembang Vending Machine
 * @version 2.0
 * @since 2024
 */
public class MesinPenjual {

    /**
     * Daftar semua barang yang tersedia di vending machine
     */
    private List<Barang> daftarBarang;

    /**
     * Riwayat semua transaksi yang telah berhasil dilakukan
     */
    private List<Transaksi> riwayatTransaksi;

    /**
     * Constructor untuk membuat objek MesinPenjual baru.
     * Menginisialisasi daftar barang dan riwayat transaksi.
     * Memuat data dari file jika ada.
     */
    public MesinPenjual() {
        this.riwayatTransaksi = new ArrayList<>();

        // Coba muat data dari file
        List<Barang> loadedData = DataPersistence.loadData();
        if (loadedData != null && !loadedData.isEmpty()) {
            this.daftarBarang = loadedData;
            System.out.println("Data produk dimuat dari file: " + daftarBarang.size() + " produk");
        } else {
            this.daftarBarang = new ArrayList<>();
            System.out.println("Tidak ada data tersimpan, menggunakan daftar kosong");
        }
    }

    /**
     * Menambahkan barang baru ke dalam inventaris.
     *
     * @param barang Objek Barang yang akan ditambahkan
     * @throws IllegalArgumentException jika barang null atau ID sudah ada
     */
    public void tambahBarang(Barang barang) {
        if (barang == null) {
            throw new IllegalArgumentException("Barang tidak boleh null!");
        }

        // Periksa apakah ID barang sudah ada
        if (cariBarang(barang.getIdBarang()) != null) {
            throw new IllegalArgumentException("Barang dengan ID " + barang.getIdBarang() + " sudah ada!");
        }

        daftarBarang.add(barang);

        // Simpan data ke file
        saveData();
    }

    /**
     * Menghapus barang dari inventaris berdasarkan ID.
     *
     * @param idBarang ID barang yang akan dihapus
     * @return true jika berhasil dihapus, false jika tidak ditemukan
     */
    public boolean hapusBarang(String idBarang) {
        Barang barang = cariBarang(idBarang);
        if (barang != null) {
            boolean removed = daftarBarang.remove(barang);
            if (removed) {
                // Simpan data ke file
                saveData();
            }
            return removed;
        }
        return false;
    }

    /**
     * Memperbarui data barang yang sudah ada dengan data baru.
     *
     * @param idBarang ID barang yang akan diperbarui
     * @param dataBaru Objek Barang dengan data yang telah diperbarui
     * @throws IllegalArgumentException jika barang tidak ditemukan
     */
    public void updateBarang(String idBarang, Barang dataBaru) {
        if (dataBaru == null) {
            throw new IllegalArgumentException("Data baru tidak boleh null!");
        }

        Barang barang = cariBarang(idBarang);
        if (barang == null) {
            throw new IllegalArgumentException("Barang dengan ID " + idBarang + " tidak ditemukan!");
        }

        // Perbarui semua atribut barang
        barang.setIdBarang(dataBaru.getIdBarang());
        barang.setNamaBarang(dataBaru.getNamaBarang());
        barang.setHargaBarang(dataBaru.getHargaBarang());
        barang.setStokSekarang(dataBaru.getStokSekarang());
        barang.setPathGambar(dataBaru.getPathGambar());

        // Simpan data ke file
        saveData();
    }

    /**
     * Mencari barang berdasarkan ID.
     *
     * @param idBarang ID barang yang dicari
     * @return Objek Barang jika ditemukan, null jika tidak ditemukan
     */
    public Barang cariBarang(String idBarang) {
        if (idBarang == null) {
            return null;
        }

        for (Barang barang : daftarBarang) {
            if (barang.getIdBarang().equals(idBarang)) {
                return barang;
            }
        }
        return null;
    }

    /**
     * Mendapatkan seluruh daftar barang yang tersedia.
     *
     * @return List berisi semua barang
     */
    public List<Barang> getDaftarBarang() {
        return new ArrayList<>(daftarBarang); // Mengembalikan salinan untuk enkapsulasi
    }

    /**
     * Mendapatkan riwayat semua transaksi.
     *
     * @return List berisi semua transaksi
     */
    public List<Transaksi> getRiwayatTransaksi() {
        return new ArrayList<>(riwayatTransaksi); // Mengembalikan salinan untuk enkapsulasi
    }

    /**
     * Mendapatkan daftar transaksi (alias untuk getRiwayatTransaksi).
     *
     * @return List berisi semua transaksi
     */
    public List<Transaksi> getDaftarTransaksi() {
        return getRiwayatTransaksi();
    }

    /**
     * Menghapus semua riwayat transaksi.
     */
    public void clearTransactionHistory() {
        riwayatTransaksi.clear();
    }

    /**
     * Memproses pembelian barang.
     * Validasi stok, kurangi stok, dan catat transaksi.
     *
     * @param barang Objek Barang yang akan dibeli
     * @throws IllegalArgumentException jika barang null atau tidak valid
     * @throws IllegalStateException jika stok tidak tersedia
     */
    public void prosesPembelian(Barang barang) {
        // Validasi parameter input tidak boleh null
        if (barang == null) {
            throw new IllegalArgumentException("Barang tidak boleh null!");
        }

        // Cari barang di inventaris untuk memastikan barang tersedia
        Barang barangDiInventaris = cariBarang(barang.getIdBarang());
        if (barangDiInventaris == null) {
            throw new IllegalArgumentException("Barang tidak ditemukan di inventaris!");
        }

        // Periksa ketersediaan stok sebelum melakukan transaksi
        if (barangDiInventaris.getStokSekarang() <= 0) {
            throw new IllegalStateException("Stok barang " + barangDiInventaris.getNamaBarang() + " habis!");
        }

        // Kurangi stok barang sebanyak 1 unit
        barangDiInventaris.kurangiStok(1);

        // Buat objek transaksi baru untuk mencatat pembelian
        Transaksi transaksi = new Transaksi(barangDiInventaris, 1);
        transaksi.setStatus(Transaksi.StatusTransaksi.BERHASIL);
        
        // Tambahkan transaksi ke riwayat untuk tracking
        riwayatTransaksi.add(transaksi);

        // Simpan perubahan data ke file untuk persistensi
        saveData();
    }

    /**
     * Mendapatkan jumlah total barang yang tersedia.
     *
     * @return Jumlah jenis barang yang berbeda
     */
    public int getJumlahJenisBarang() {
        return daftarBarang.size();
    }

    /**
     * Mendapatkan jumlah total transaksi yang telah dilakukan.
     *
     * @return Jumlah transaksi
     */
    public int getJumlahTransaksi() {
        return riwayatTransaksi.size();
    }

    /**
     * Menyimpan data barang ke file.
     * Dipanggil otomatis setiap kali ada perubahan data.
     */
    private void saveData() {
        DataPersistence.saveData(daftarBarang);
    }

    /**
     * Memuat ulang data dari file.
     */
    public void reloadData() {
        List<Barang> loadedData = DataPersistence.loadData();
        if (loadedData != null) {
            daftarBarang.clear();
            daftarBarang.addAll(loadedData);
        }
    }
}