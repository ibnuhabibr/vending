package com.vendingmachine.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kelas Transaksi merepresentasikan satu record pembelian.
 * Setiap transaksi mencatat barang yang dibeli, kuantitas, status, dan waktu transaksi.
 *
 * @author Senior Java Developer
 * @version 2.0
 */
public class Transaksi implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Enum untuk status transaksi
     */
    public enum StatusTransaksi {
        BERHASIL("Berhasil"),
        BATAL("Batal"),
        PENDING("Pending");

        private final String displayName;

        StatusTransaksi(String displayName) {
            this.displayName = displayName;
        }

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
