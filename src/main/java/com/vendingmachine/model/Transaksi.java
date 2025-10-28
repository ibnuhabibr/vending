package com.vendingmachine.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kelas model untuk merepresentasikan transaksi pembelian dalam vending machine.
 * Setiap transaksi mencatat informasi lengkap tentang pembelian termasuk
 * produk yang dibeli, kuantitas, status, waktu, dan total harga.
 * 
 * Kelas ini mengimplementasikan Serializable untuk mendukung
 * penyimpanan riwayat transaksi ke file.
 * 
 * @author Tim Pengembang Vending Machine
 * @version 2.0
 * @since 2024
 */
public class Transaksi implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Enum untuk status transaksi dalam sistem vending machine.
     * Menentukan kondisi akhir dari sebuah transaksi pembelian.
     * 
     * Status yang tersedia:
     * - BERHASIL: Transaksi berhasil diselesaikan
     * - BATAL: Transaksi dibatalkan oleh sistem atau pengguna
     * - PENDING: Transaksi sedang dalam proses
     * 
     * @author Tim Pengembang Vending Machine
     * @version 1.0
     * @since 2024
     */
    public enum StatusTransaksi {
        /** Transaksi berhasil diselesaikan */
        BERHASIL("Berhasil"),
        
        /** Transaksi dibatalkan oleh sistem atau pengguna */
        BATAL("Batal"),
        
        /** Transaksi sedang dalam proses */
        PENDING("Pending");

        /** Nama tampilan yang user-friendly untuk status */
        private final String displayName;

        /**
         * Konstruktor untuk StatusTransaksi.
         * 
         * @param displayName Nama tampilan yang akan ditampilkan ke pengguna
         */
        StatusTransaksi(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Mendapatkan nama tampilan yang user-friendly untuk status transaksi.
         * 
         * @return String nama tampilan status transaksi
         */
        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * ID unik transaksi yang di-generate otomatis
     */
    private String idTransaksi;

    /**
     * Objek barang yang dibeli dalam transaksi ini
     */
    private Barang barangYangDibeli;

    /**
     * Jumlah barang yang dibeli
     */
    private int kuantitas;

    /**
     * Status transaksi (berhasil/batal/pending)
     */
    private StatusTransaksi status;

    /**
     * Waktu kapan transaksi terjadi
     */
    private LocalDateTime waktuTransaksi;

    /**
     * Constructor untuk membuat objek Transaksi baru.
     * ID transaksi akan di-generate otomatis dengan format TRX-timestamp.
     *
     * @param barangYangDibeli Objek Barang yang dibeli
     * @param kuantitas Jumlah barang yang dibeli
     */
    public Transaksi(Barang barangYangDibeli, int kuantitas) {
        this.barangYangDibeli = barangYangDibeli;
        this.kuantitas = kuantitas;
        this.status = StatusTransaksi.PENDING;
        this.waktuTransaksi = LocalDateTime.now();
        this.idTransaksi = generateIdTransaksi();
    }

    /**
     * Constructor untuk backward compatibility
     *
     * @param barangYangDibeli Objek Barang yang dibeli
     */
    public Transaksi(Barang barangYangDibeli) {
        this(barangYangDibeli, 1);
    }

    /**
     * Menghasilkan ID transaksi unik berdasarkan timestamp.
     *
     * @return ID transaksi dengan format TRX-yyyyMMddHHmmssSSS
     */
    private String generateIdTransaksi() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return "TRX-" + waktuTransaksi.format(formatter);
    }

    /**
     * Mendapatkan ID transaksi.
     *
     * @return ID transaksi
     */
    public String getIdTransaksi() {
        return idTransaksi;
    }

    /**
     * Mendapatkan barang yang dibeli.
     *
     * @return Objek Barang yang dibeli
     */
    public Barang getBarangYangDibeli() {
        return barangYangDibeli;
    }

    /**
     * Mendapatkan waktu transaksi.
     *
     * @return LocalDateTime kapan transaksi terjadi
     */
    public LocalDateTime getWaktuTransaksi() {
        return waktuTransaksi;
    }

    /**
     * Mendapatkan kuantitas barang yang dibeli.
     *
     * @return Jumlah barang yang dibeli
     */
    public int getKuantitas() {
        return kuantitas;
    }

    /**
     * Mengatur kuantitas barang yang dibeli.
     *
     * @param kuantitas Jumlah barang yang dibeli
     */
    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }

    /**
     * Mendapatkan status transaksi.
     *
     * @return Status transaksi
     */
    public StatusTransaksi getStatus() {
        return status;
    }

    /**
     * Mengatur status transaksi.
     *
     * @param status Status transaksi baru
     */
    public void setStatus(StatusTransaksi status) {
        this.status = status;
    }

    /**
     * Mendapatkan total harga transaksi (harga barang x kuantitas).
     *
     * @return Total harga transaksi
     */
    public double getTotalHarga() {
        return barangYangDibeli.getHargaBarang() * kuantitas;
    }

    /**
     * Mendapatkan detail transaksi dalam format String yang mudah dibaca.
     *
     * @return String berisi ringkasan transaksi
     */
    public String getDetailTransaksi() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return String.format(
            "=== DETAIL TRANSAKSI ===\n" +
            "ID Transaksi  : %s\n" +
            "Barang        : %s\n" +
            "Harga Satuan  : Rp %.2f\n" +
            "Kuantitas     : %d\n" +
            "Total Harga   : Rp %.2f\n" +
            "Status        : %s\n" +
            "Waktu         : %s\n" +
            "========================",
            idTransaksi,
            barangYangDibeli.getNamaBarang(),
            barangYangDibeli.getHargaBarang(),
            kuantitas,
            getTotalHarga(),
            status.getDisplayName(),
            waktuTransaksi.format(formatter)
        );
    }

    /**
     * Mengembalikan representasi String dari objek Transaksi.
     *
     * @return String berisi informasi transaksi
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return String.format("Transaksi[ID=%s, Barang=%s, Waktu=%s]",
                           idTransaksi,
                           barangYangDibeli.getNamaBarang(),
                           waktuTransaksi.format(formatter));
    }
}