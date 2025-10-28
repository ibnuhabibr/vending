package com.vendingmachine.controller;

import com.vendingmachine.model.Transaksi;
import com.vendingmachine.model.Barang;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller untuk menampilkan detail transaksi individual.
 * Menampilkan informasi lengkap tentang satu transaksi tertentu
 * termasuk data produk, waktu, dan status transaksi.
 * 
 * Fitur yang dikelola:
 * - Tampilan detail transaksi lengkap
 * - Informasi produk yang dibeli
 * - Waktu dan tanggal transaksi
 * - Status transaksi
 * - Navigasi kembali ke riwayat transaksi
 * 
 * @author Tim Pengembang Vending Machine
 * @version 1.0
 * @since 2024
 */
public class TransactionDetailController implements Initializable {

    @FXML private ImageView productImageView;
    @FXML private Label transactionIdLabel;
    @FXML private Label productNameLabel;
    @FXML private Label unitPriceLabel;
    @FXML private Label quantityLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label transactionTimeLabel;
    @FXML private Label statusLabel;
    @FXML private Button printReceiptButton;
    @FXML private Button closeButton;

    private Transaksi transaksi;
    private Barang barang;

    /**
     * Inisialisasi controller dan setup komponen UI.
     * Dipanggil otomatis setelah FXML dimuat.
     *
     * @param location URL lokasi FXML
     * @param resources ResourceBundle untuk lokalisasi
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inisialisasi state awal
    }

    /**
     * Mengatur data transaksi yang akan ditampilkan di UI.
     *
     * @param transaksi Objek transaksi yang akan ditampilkan
     * @param barang Objek barang untuk menampilkan gambar produk
     */
    public void setTransactionData(Transaksi transaksi, Barang barang) {
        this.transaksi = transaksi;
        this.barang = barang;
        populateTransactionDetails();
    }

    /**
     * Mengisi detail transaksi ke dalam komponen UI.
     * Menampilkan ID transaksi, detail produk, waktu, status, dan gambar.
     */
    private void populateTransactionDetails() {
        if (transaksi == null) return;

        // Atur ID transaksi
        transactionIdLabel.setText(transaksi.getIdTransaksi());

        // Atur detail produk
        productNameLabel.setText(transaksi.getBarangYangDibeli().getNamaBarang());
        unitPriceLabel.setText(formatCurrency(transaksi.getBarangYangDibeli().getHargaBarang()));
        quantityLabel.setText(String.valueOf(transaksi.getKuantitas()));
        totalPriceLabel.setText(formatCurrency(transaksi.getTotalHarga()));

        // Atur waktu transaksi
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        transactionTimeLabel.setText(transaksi.getWaktuTransaksi().format(formatter));

        // Atur status
        statusLabel.setText(transaksi.getStatus().getDisplayName());

        // Atur gambar produk
        loadProductImage();
    }

    /**
     * Memuat gambar produk ke ImageView.
     * Mencoba memuat dari resources terlebih dahulu, kemudian dari file system.
     * Jika gagal, akan memuat gambar default.
     */
    private void loadProductImage() {
        try {
            String imagePath = barang.getPathGambar();
            
            // Coba muat dari resources terlebih dahulu
            URL imageUrl = getClass().getResource("/images/" + imagePath);
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toExternalForm());
                productImageView.setImage(image);
            } else {
                // Coba muat dari file system
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    productImageView.setImage(image);
                } else {
                    // Muat gambar default
                    loadDefaultImage();
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading product image: " + e.getMessage());
            loadDefaultImage();
        }
    }

    /**
     * Memuat gambar default jika gambar produk tidak ditemukan.
     * Digunakan sebagai fallback ketika gambar produk tidak tersedia.
     */
    private void loadDefaultImage() {
        try {
            URL defaultImageUrl = getClass().getResource("/images/default.png");
            if (defaultImageUrl != null) {
                Image defaultImage = new Image(defaultImageUrl.toExternalForm());
                productImageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
        }
    }

    /**
     * Memformat angka menjadi format mata uang Rupiah.
     *
     * @param amount Jumlah yang akan diformat
     * @return String dalam format mata uang Rupiah
     */
    private String formatCurrency(double amount) {
        return String.format("Rp %.0f", amount);
    }

    /**
     * Menangani aksi tombol cetak struk.
     * Menampilkan dialog dengan detail struk pembayaran.
     */
    @FXML
    private void handlePrintReceipt() {
        // Simulate printing receipt
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cetak Struk");
        alert.setHeaderText("Struk Pembayaran");
        alert.setContentText(generateReceiptText());
        
        // Style the dialog
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("transaction-detail-dialog");
        
        alert.showAndWait();
    }

    /**
     * Menghasilkan teks untuk struk pembayaran.
     * Berisi detail lengkap transaksi dalam format yang mudah dibaca.
     *
     * @return String berisi detail struk pembayaran
     */
    private String generateReceiptText() {
        if (transaksi == null) return "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        return String.format(
            "========== PENSHOP ==========\n" +
            "      Vending Machine\n" +
            "=============================\n\n" +
            "ID Transaksi : %s\n" +
            "Tanggal      : %s\n\n" +
            "DETAIL PEMBELIAN:\n" +
            "Produk       : %s\n" +
            "Harga Satuan : %s\n" +
            "Kuantitas    : %d\n" +
            "-----------------------------\n" +
            "TOTAL        : %s\n" +
            "Status       : %s\n\n" +
            "=============================\n" +
            "Terima kasih telah berbelanja!\n" +
            "=============================",
            transaksi.getIdTransaksi(),
            transaksi.getWaktuTransaksi().format(formatter),
            transaksi.getBarangYangDibeli().getNamaBarang(),
            formatCurrency(transaksi.getBarangYangDibeli().getHargaBarang()),
            transaksi.getKuantitas(),
            formatCurrency(transaksi.getTotalHarga()),
            transaksi.getStatus().getDisplayName()
        );
    }

    /**
     * Menangani aksi tombol tutup dialog.
     * Menutup window detail transaksi.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}