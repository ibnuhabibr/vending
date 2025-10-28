package com.vendingmachine;

import com.vendingmachine.controller.MainController;
import com.vendingmachine.model.Barang;
import com.vendingmachine.model.MesinPenjual;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Kelas utama aplikasi Vending Machine (Mesin Penjual Otomatis).
 * Aplikasi ini menggunakan JavaFX untuk antarmuka pengguna dan menyediakan
 * fitur-fitur seperti pembelian produk, manajemen admin, dan riwayat transaksi.
 * 
 * Fitur utama:
 * - Tampilan produk dengan gambar dan informasi lengkap
 * - Sistem pembayaran QRIS
 * - Panel admin untuk manajemen produk
 * - Riwayat transaksi dengan detail lengkap
 * - Persistensi data menggunakan serialisasi
 * 
 * @author Tim Pengembang Vending Machine
 * @version 1.0
 * @since 2024
 */
public class MainApp extends Application {

    private MesinPenjual mesinPenjual;

    /**
     * Metode start yang dipanggil saat aplikasi dimulai.
     *
     * @param primaryStage Stage utama aplikasi
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Inisialisasi MesinPenjual (akan memuat otomatis dari file jika ada)
            mesinPenjual = new MesinPenjual();

            // Muat data awal hanya jika belum ada data tersimpan
            if (mesinPenjual.getJumlahJenisBarang() == 0) {
                loadInitialData();
            }

            // Muat file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vendingmachine/MainView.fxml"));
            Parent root = loader.load();

            // Atur MesinPenjual ke controller
            MainController mainController = loader.getController();
            mainController.setMesinPenjual(mesinPenjual);

            // Atur scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            // Atur stage
            primaryStage.setTitle("Vending Machine - QR Payment System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
        }
    }

    /**
     * Memuat data awal produk untuk vending machine.
     */
    private void loadInitialData() {
        // Menambahkan produk-produk contoh
        mesinPenjual.tambahBarang(new Barang(
            "P001",
            "Kopi Hitam",
            10000,
            15,
            "/images/kopi.png"
        ));

        mesinPenjual.tambahBarang(new Barang(
            "P002",
            "Teh Manis",
            8000,
            20,
            "/images/teh.png"
        ));

        mesinPenjual.tambahBarang(new Barang(
            "P003",
            "Air Mineral",
            5000,
            25,
            "/images/air.png"
        ));

        mesinPenjual.tambahBarang(new Barang(
            "P004",
            "Susu",
            12000,
            10,
            "/images/susu.png"
        ));

        mesinPenjual.tambahBarang(new Barang(
            "P005",
            "Jus",
            15000,
            12,
            "/images/jus.png"
        ));

        mesinPenjual.tambahBarang(new Barang(
            "P006",
            "Soda",
            9000,
            18,
            "/images/soda.png"
        ));

        System.out.println("Data awal berhasil dimuat: " +
                         mesinPenjual.getJumlahJenisBarang() + " jenis produk");
    }

    /**
     * Entry point aplikasi.
     *
     * @param args Argumen command line
     */
    public static void main(String[] args) {
        launch(args);
    }
}
