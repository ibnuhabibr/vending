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
 * Controller untuk mengelola tampilan detail transaksi setelah pembayaran berhasil.
 * Menampilkan informasi lengkap tentang transaksi yang baru saja dilakukan.
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup initial state
    }

    /**
     * Set data transaksi yang akan ditampilkan.
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
     */
    private void populateTransactionDetails() {
        if (transaksi == null) return;

        // Set transaction ID
        transactionIdLabel.setText(transaksi.getIdTransaksi());

        // Set product details
        productNameLabel.setText(transaksi.getBarangYangDibeli().getNamaBarang());
        unitPriceLabel.setText(formatCurrency(transaksi.getBarangYangDibeli().getHargaBarang()));
        quantityLabel.setText(String.valueOf(transaksi.getKuantitas()));
        totalPriceLabel.setText(formatCurrency(transaksi.getTotalHarga()));

        // Set transaction time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        transactionTimeLabel.setText(transaksi.getWaktuTransaksi().format(formatter));

        // Set status
        statusLabel.setText(transaksi.getStatus().getDisplayName());

        // Set product image
        loadProductImage();
    }

    /**
     * Load gambar produk ke ImageView.
     */
    private void loadProductImage() {
        try {
            String imagePath = barang.getPathGambar();
            
            // Try to load from resources first
            URL imageUrl = getClass().getResource("/images/" + imagePath);
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toExternalForm());
                productImageView.setImage(image);
            } else {
                // Try to load from file system
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    productImageView.setImage(image);
                } else {
                    // Load default image
                    loadDefaultImage();
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading product image: " + e.getMessage());
            loadDefaultImage();
        }
    }

    /**
     * Load gambar default jika gambar produk tidak ditemukan.
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
     * Format mata uang Indonesia.
     *
     * @param amount Jumlah yang akan diformat
     * @return String format mata uang
     */
    private String formatCurrency(double amount) {
        return String.format("Rp %.0f", amount);
    }

    /**
     * Handle tombol cetak struk.
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
     * Generate text untuk struk pembayaran.
     *
     * @return String berisi detail struk
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
     * Handle tombol tutup dialog.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}