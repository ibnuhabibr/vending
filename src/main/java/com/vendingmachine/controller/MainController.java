package com.vendingmachine.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.vendingmachine.model.Barang;
import com.vendingmachine.model.MesinPenjual;
import com.vendingmachine.controller.TransactionHistoryController;
import com.vendingmachine.controller.TransactionDetailController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller utama untuk tampilan vending machine.
 * Mengelola interaksi pengguna dengan produk, proses pembelian,
 * dan navigasi ke fitur-fitur lain seperti admin dan riwayat transaksi.
 * 
 * Fitur yang dikelola:
 * - Tampilan grid produk dengan gambar dan informasi
 * - Proses pembelian dengan validasi stok
 * - Dialog pembayaran QRIS
 * - Navigasi ke panel admin dan riwayat transaksi
 * - Refresh otomatis data produk
 * 
 * @author Tim Pengembang Vending Machine
 * @version 1.0
 * @since 2024
 */
public class MainController {

    @FXML
    private TilePane productTilePane;

    @FXML
    private Button adminButton;

    @FXML
    private Button historyButton;

    private MesinPenjual mesinPenjual;

    /**
     * Inisialisasi controller.
     * Dipanggil otomatis setelah FXML dimuat.
     */
    @FXML
    public void initialize() {
        // Konfigurasi TilePane untuk menampilkan produk
        productTilePane.setHgap(15);
        productTilePane.setVgap(15);
        productTilePane.setPadding(new Insets(20));
        productTilePane.setPrefColumns(3);
    }

    /**
     * Mengatur instance MesinPenjual dan memuat data produk.
     *
     * @param mesinPenjual Instance MesinPenjual yang berisi data
     */
    public void setMesinPenjual(MesinPenjual mesinPenjual) {
        this.mesinPenjual = mesinPenjual;
        loadProducts();
    }

    /**
     * Memuat dan menampilkan semua produk dari MesinPenjual.
     */
    private void loadProducts() {
        productTilePane.getChildren().clear();

        for (Barang barang : mesinPenjual.getDaftarBarang()) {
            VBox productCard = createProductCard(barang);
            productTilePane.getChildren().add(productCard);
        }
    }

    /**
     * Membuat kartu produk untuk satu barang.
     *
     * @param barang Objek Barang yang akan ditampilkan
     * @return VBox berisi kartu produk
     */
    private VBox createProductCard(Barang barang) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("product-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);
        card.setPrefHeight(280);

        // Gambar produk
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        try {
            InputStream imageStream = getClass().getResourceAsStream(barang.getPathGambar());
            if (imageStream != null) {
                Image image = new Image(imageStream);
                imageView.setImage(image);
            } else {
                // Gambar default jika tidak ditemukan
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + barang.getPathGambar());
        }

        // Label nama produk
        Label nameLabel = new Label(barang.getNamaBarang());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(180);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle("-fx-alignment: center;");

        // Label harga
        Label priceLabel = new Label(formatCurrency(barang.getHargaBarang()));
        priceLabel.getStyleClass().add("product-price");

        // Label stok
        Label stockLabel = new Label("Stok: " + barang.getStokSekarang());
        stockLabel.getStyleClass().add("product-stock");

        // Tombol beli
        Button buyButton = new Button("BELI");
        buyButton.getStyleClass().add("buy-button");
        buyButton.setMaxWidth(Double.MAX_VALUE);
        buyButton.setOnAction(e -> handleBuyButton(barang));

        // Nonaktifkan tombol jika stok habis
        if (barang.getStokSekarang() <= 0) {
            buyButton.setDisable(true);
            buyButton.setText("STOK HABIS");
            stockLabel.setStyle("-fx-text-fill: #e74c3c;");
        }

        card.getChildren().addAll(imageView, nameLabel, priceLabel, stockLabel, buyButton);

        return card;
    }

    /**
     * Menangani aksi tombol beli.
     *
     * @param barang Barang yang akan dibeli
     */
    private void handleBuyButton(Barang barang) {
        // Cek stok terlebih dahulu
        if (barang.getStokSekarang() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Stok Habis",
                     "Maaf, " + barang.getNamaBarang() + " sedang habis!");
            return;
        }

        // Konfirmasi pembelian
        Optional<ButtonType> result = showConfirmation(
            "Konfirmasi Pembelian",
            "Apakah Anda yakin ingin membeli " + barang.getNamaBarang() + "?",
            "Harga: " + formatCurrency(barang.getHargaBarang())
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Tampilkan QR Code untuk pembayaran
            showQRCodePayment(barang);
        }
    }

    /**
     * Menampilkan jendela QR Code untuk pembayaran dengan verifikasi.
     *
     * @param barang Barang yang dibeli
     */
    private void showQRCodePayment(Barang barang) {
        // Membuat stage baru untuk dialog pembayaran QRIS
        Stage qrStage = new Stage();
        qrStage.setTitle("Pembayaran QRIS - Vending Machine");
        qrStage.initModality(Modality.APPLICATION_MODAL); // Modal agar user fokus pada pembayaran

        // Container utama untuk layout pembayaran
        VBox qrBox = new VBox(20);
        qrBox.setAlignment(Pos.CENTER);
        qrBox.setPadding(new Insets(40));
        qrBox.getStyleClass().add("qr-container");

        // Judul dialog pembayaran
        Label titleLabel = new Label("Scan QRIS untuk Pembayaran");
        titleLabel.getStyleClass().add("qr-title");

        // Informasi produk yang dibeli
        Label infoLabel = new Label(barang.getNamaBarang());
        infoLabel.getStyleClass().add("qr-product-name");

        Label priceLabel = new Label(formatCurrency(barang.getHargaBarang()));
        priceLabel.getStyleClass().add("qr-price");

        // Gambar QR Code untuk pembayaran
        ImageView qrImageView = new ImageView();
        qrImageView.setFitWidth(320);
        qrImageView.setFitHeight(320);
        qrImageView.setPreserveRatio(true);

        // Memuat gambar QR Code dari resources
        try {
            InputStream qrStream = getClass().getResourceAsStream("/images/qris_payment.png");
            if (qrStream != null) {
                Image qrImage = new Image(qrStream);
                qrImageView.setImage(qrImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading QR code image");
        }

        // Instruksi untuk user
        Label instructionLabel = new Label("Scan QR Code di atas menggunakan aplikasi pembayaran Anda");
        instructionLabel.getStyleClass().add("qr-instruction");
        instructionLabel.setWrapText(true);
        instructionLabel.setMaxWidth(350);

        // Tombol untuk verifikasi dan pembatalan
        Button verifyButton = new Button("✓ Verifikasi Pembayaran");
        verifyButton.getStyleClass().add("verify-button");

        Button cancelButton = new Button("✕ Batalkan Pesanan");
        cancelButton.getStyleClass().add("cancel-button");

        // Event handler untuk tombol verifikasi
        verifyButton.setOnAction(e -> {
            handlePaymentVerification(qrStage, barang, qrBox, verifyButton, cancelButton);
        });

        // Event handler untuk tombol batal dengan konfirmasi
        cancelButton.setOnAction(e -> {
            Optional<ButtonType> result = showConfirmation(
                "Batalkan Pesanan",
                "Apakah Anda yakin ingin membatalkan pesanan?",
                "Pesanan akan dibatalkan dan Anda kembali ke menu utama."
            );

            if (result.isPresent() && result.get() == ButtonType.OK) {
                qrStage.close();
            }
        });

        // Menambahkan semua komponen ke container
        qrBox.getChildren().addAll(titleLabel, infoLabel, priceLabel, qrImageView,
                                   instructionLabel, verifyButton, cancelButton);

        Scene qrScene = new Scene(qrBox, 500, 750);
        qrScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        qrStage.setScene(qrScene);
        qrStage.setResizable(false);
        qrStage.showAndWait();
    }

    /**
     * Menangani verifikasi pembayaran dengan loading effect.
     */
    private void handlePaymentVerification(Stage qrStage, Barang barang, VBox qrBox,
                                          Button verifyButton, Button cancelButton) {
        // Disable buttons
        verifyButton.setDisable(true);
        cancelButton.setDisable(true);

        // Clear content dan tampilkan loading
        qrBox.getChildren().clear();
        qrBox.setAlignment(Pos.CENTER);

        Label loadingTitle = new Label("Memverifikasi Pembayaran...");
        loadingTitle.getStyleClass().add("loading-title");

        // Simple loading animation using label
        Label loadingDots = new Label("");
        loadingDots.getStyleClass().add("loading-dots");

        // Progress indicator text
        Label progressLabel = new Label("Menghubungi server pembayaran");
        progressLabel.getStyleClass().add("loading-text");

        qrBox.getChildren().addAll(loadingTitle, loadingDots, progressLabel);

        // Simulasi loading dengan Timeline
        javafx.animation.Timeline dotsAnimation = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(500), evt -> {
                String dots = loadingDots.getText();
                if (dots.length() < 3) {
                    loadingDots.setText(dots + ".");
                } else {
                    loadingDots.setText("");
                }
            })
        );
        dotsAnimation.setCycleCount(javafx.animation.Animation.INDEFINITE);
        dotsAnimation.play();

        // Update progress text
        javafx.animation.Timeline progressAnimation = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0.8), evt ->
                progressLabel.setText("Menghubungi server pembayaran")),
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1.6), evt ->
                progressLabel.setText("Memvalidasi transaksi")),
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(2.4), evt ->
                progressLabel.setText("Memproses pembayaran"))
        );
        progressAnimation.play();

        // Setelah 3 detik, proses pembayaran
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
            javafx.util.Duration.seconds(3.2)
        );

        pause.setOnFinished(evt -> {
            dotsAnimation.stop();

            try {
                // Proses pembelian (kurangi stok)
                mesinPenjual.prosesPembelian(barang);

                // Tampilkan success message
                showPaymentSuccess(qrStage, barang, qrBox);

            } catch (Exception ex) {
                // Jika gagal, tampilkan error
                showAlert(Alert.AlertType.ERROR, "Error",
                         "Terjadi kesalahan: " + ex.getMessage());
                qrStage.close();
            }
        });

        pause.play();
    }

    /**
     * Menampilkan pesan sukses pembayaran dan detail transaksi.
     */
    private void showPaymentSuccess(Stage qrStage, Barang barang, VBox qrBox) {
        qrBox.getChildren().clear();
        qrBox.setAlignment(Pos.CENTER);
        qrBox.setPadding(new Insets(50));

        // Success icon (menggunakan karakter Unicode)
        Label successIcon = new Label("✓");
        successIcon.getStyleClass().add("success-icon");

        Label successTitle = new Label("Pembayaran Berhasil!");
        successTitle.getStyleClass().add("success-title");

        Label successMessage = new Label("Terima kasih telah berbelanja");
        successMessage.getStyleClass().add("success-message");

        // Product info
        Label productInfo = new Label(barang.getNamaBarang());
        productInfo.getStyleClass().add("success-product");

        Label instruction = new Label("Silakan ambil produk Anda di slot pengambilan");
        instruction.getStyleClass().add("success-instruction");
        instruction.setWrapText(true);
        instruction.setMaxWidth(350);

        Button detailButton = new Button("Lihat Detail Transaksi");
        detailButton.getStyleClass().add("detail-button");
        detailButton.setOnAction(e -> {
            showTransactionDetail(barang);
        });

        Button closeButton = new Button("Selesai");
        closeButton.getStyleClass().add("success-button");
        closeButton.setOnAction(e -> {
            qrStage.close();
            // Refresh tampilan produk
            loadProducts();
        });

        qrBox.getChildren().addAll(successIcon, successTitle, successMessage,
                                   productInfo, instruction, detailButton, closeButton);
    }

    /**
     * Menampilkan detail transaksi setelah pembelian berhasil.
     */
    private void showTransactionDetail(Barang barang) {
        try {
            // Ambil transaksi terakhir dari riwayat
            var riwayatTransaksi = mesinPenjual.getRiwayatTransaksi();
            if (!riwayatTransaksi.isEmpty()) {
                var transaksiTerakhir = riwayatTransaksi.get(riwayatTransaksi.size() - 1);
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vendingmachine/TransactionDetailView.fxml"));
                Parent root = loader.load();

                TransactionDetailController detailController = loader.getController();
                detailController.setTransactionData(transaksiTerakhir, barang);

                Stage detailStage = new Stage();
                detailStage.setTitle("Detail Transaksi - Vending Machine");
                detailStage.initModality(Modality.APPLICATION_MODAL);

                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                detailStage.setScene(scene);
                detailStage.showAndWait();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                     "Gagal membuka Detail Transaksi: " + e.getMessage());
        }
    }

    /**
     * Menangani aksi tombol Admin.
     */
    @FXML
    private void handleAdminButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vendingmachine/AdminView.fxml"));
            Parent root = loader.load();

            AdminController adminController = loader.getController();
            adminController.setMesinPenjual(mesinPenjual);
            adminController.setMainController(this);

            Stage adminStage = new Stage();
            adminStage.setTitle("Admin Panel - Vending Machine");
            adminStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            adminStage.setScene(scene);
            adminStage.showAndWait();

            // Refresh produk setelah admin ditutup
            loadProducts();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                     "Gagal membuka Admin Panel: " + e.getMessage());
        }
    }

    /**
     * Menangani aksi tombol Riwayat Transaksi.
     */
    @FXML
    private void handleHistoryButton() {
        try {
            // Debug: Print resource URL
            java.net.URL fxmlUrl = getClass().getResource("/com/vendingmachine/TransactionHistoryView.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            
            if (fxmlUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "File TransactionHistoryView.fxml tidak ditemukan di classpath");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            TransactionHistoryController historyController = loader.getController();
            historyController.setMesinPenjual(mesinPenjual);

            Stage historyStage = new Stage();
            historyStage.setTitle("Riwayat Transaksi - Vending Machine");
            historyStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            historyStage.setScene(scene);
            historyStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                     "Gagal membuka Riwayat Transaksi: " + e.getMessage());
        }
    }

    /**
     * Memformat angka menjadi format mata uang Rupiah.
     *
     * @param amount Jumlah yang akan diformat
     * @return String dengan format Rupiah (contoh: Rp10.000)
     */
    private String formatCurrency(double amount) {
        // Format dengan pemisah ribuan tanpa desimal
        return "Rp" + String.format("%,.0f", amount).replace(",", ".");
    }

    /**
     * Menampilkan alert dialog.
     *
     * @param type Tipe alert
     * @param title Judul alert
     * @param content Konten alert
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog konfirmasi.
     *
     * @param title Judul dialog
     * @param header Header dialog
     * @param content Konten dialog
     * @return Optional berisi ButtonType yang dipilih
     */
    private Optional<ButtonType> showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    /**
     * Refresh tampilan produk (dipanggil dari AdminController).
     */
    public void refreshProducts() {
        loadProducts();
    }
}