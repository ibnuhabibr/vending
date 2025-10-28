package com.vendingmachine.controller;

import com.vendingmachine.model.MesinPenjual;
import com.vendingmachine.model.Transaksi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller untuk mengelola tampilan riwayat transaksi.
 * Menampilkan daftar semua transaksi yang telah dilakukan
 * dalam format tabel dengan informasi lengkap.
 * 
 * Fitur yang dikelola:
 * - Tampilan tabel riwayat transaksi
 * - Navigasi ke detail transaksi individual
 * - Kembali ke tampilan utama
 * - Refresh data transaksi
 * 
 * @author Tim Pengembang Vending Machine
 * @version 1.0
 * @since 2024
 */
public class TransactionHistoryController implements Initializable {

    @FXML private TableView<Transaksi> transactionTable;
    @FXML private TableColumn<Transaksi, String> idColumn;
    @FXML private TableColumn<Transaksi, String> itemColumn;
    @FXML private TableColumn<Transaksi, String> priceColumn;
    @FXML private TableColumn<Transaksi, String> quantityColumn;
    @FXML private TableColumn<Transaksi, String> totalColumn;
    @FXML private TableColumn<Transaksi, String> statusColumn;
    @FXML private TableColumn<Transaksi, String> dateColumn;
    
    @FXML private Label totalTransactionsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label successfulTransactionsLabel;
    
    @FXML private Button backButton;
    @FXML private Button refreshButton;
    @FXML private Button clearHistoryButton;

    private MesinPenjual mesinPenjual;
    private ObservableList<Transaksi> transactionData;
    private NumberFormat currencyFormat;

    /**
     * Inisialisasi controller dan setup komponen UI.
     * Dipanggil otomatis setelah FXML dimuat.
     *
     * @param location URL lokasi FXML
     * @param resources ResourceBundle untuk lokalisasi
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inisialisasi formatter mata uang
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        currencyFormat.setMaximumFractionDigits(0);
        
        // Atur kolom tabel
        setupTableColumns();
        
        // Inisialisasi data transaksi
        transactionData = FXCollections.observableArrayList();
        transactionTable.setItems(transactionData);
        
        // Atur listener seleksi tabel untuk double-click melihat detail
        transactionTable.setRowFactory(tv -> {
            TableRow<Transaksi> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showTransactionDetail(row.getItem());
                }
            });
            return row;
        });
    }

    /**
     * Mengatur kolom-kolom tabel transaksi dan event handler.
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        
        itemColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getBarangYangDibeli().getNamaBarang()
            )
        );
        
        priceColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                formatCurrency(cellData.getValue().getBarangYangDibeli().getHargaBarang())
            )
        );
        
        quantityColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getKuantitas())
            )
        );
        
        totalColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                formatCurrency(cellData.getValue().getTotalHarga())
            )
        );
        
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus().getDisplayName()
            )
        );
        
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getWaktuTransaksi().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                )
            )
        );
        
        // Atur custom cell factory untuk kolom status untuk menambahkan styling
        statusColumn.setCellFactory(column -> new TableCell<Transaksi, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("BERHASIL")) {
                        setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                    } else if (item.equals("BATAL")) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    /**
     * Mengatur referensi MesinPenjual dan memuat data transaksi.
     *
     * @param mesinPenjual Instance MesinPenjual yang berisi data transaksi
     */
    public void setMesinPenjual(MesinPenjual mesinPenjual) {
        this.mesinPenjual = mesinPenjual;
        loadTransactionData();
        updateStatistics();
    }

    /**
     * Memuat data transaksi dari MesinPenjual ke dalam tabel.
     */
    private void loadTransactionData() {
        if (mesinPenjual != null) {
            transactionData.setAll(mesinPenjual.getRiwayatTransaksi());
        }
    }

    /**
     * Memperbarui statistik transaksi yang ditampilkan di UI.
     * Menghitung total transaksi, pendapatan, dan transaksi sukses.
     */
    private void updateStatistics() {
        if (mesinPenjual == null) return;
        
        List<Transaksi> allTransactions = mesinPenjual.getRiwayatTransaksi();
        int totalTransactions = allTransactions.size();
        
        double totalRevenue = allTransactions.stream()
            .filter(t -> t.getStatus() == Transaksi.StatusTransaksi.BERHASIL)
            .mapToDouble(Transaksi::getTotalHarga)
            .sum();
            
        long successfulTransactions = allTransactions.stream()
            .filter(t -> t.getStatus() == Transaksi.StatusTransaksi.BERHASIL)
            .count();
        
        totalTransactionsLabel.setText(String.valueOf(totalTransactions));
        totalRevenueLabel.setText(formatCurrency(totalRevenue));
        successfulTransactionsLabel.setText(String.valueOf(successfulTransactions));
    }

    /**
     * Memformat angka menjadi format mata uang Rupiah.
     *
     * @param amount Jumlah yang akan diformat
     * @return String dalam format mata uang Rupiah
     */
    private String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }

    /**
     * Menampilkan detail transaksi dalam window terpisah.
     *
     * @param transaksi Objek Transaksi yang akan ditampilkan detailnya
     */
    private void showTransactionDetail(Transaksi transaksi) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vendingmachine/TransactionDetailView.fxml"));
            Parent root = loader.load();
            
            TransactionDetailController controller = loader.getController();
            controller.setTransactionData(transaksi, transaksi.getBarangYangDibeli());
            
            Stage stage = new Stage();
            stage.setTitle("Detail Transaksi - " + transaksi.getIdTransaksi());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Error", "Gagal membuka detail transaksi: " + e.getMessage());
        }
    }

    /**
     * Menangani aksi tombol kembali ke menu utama.
     */
    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vendingmachine/MainView.fxml"));
            Parent root = loader.load();
            
            // Atur MesinPenjual ke MainController
            MainController mainController = loader.getController();
            mainController.setMesinPenjual(mesinPenjual);
            
            // Ganti scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Vending Machine - QR Payment System");
            
        } catch (IOException e) {
            showError("Error", "Gagal kembali ke menu utama: " + e.getMessage());
        }
    }

    /**
     * Menangani aksi tombol refresh untuk memuat ulang data transaksi.
     */
    @FXML
    private void handleRefresh() {
        if (mesinPenjual != null) {
            loadTransactionData();
            updateStatistics();
            showInfo("Refresh", "Data transaksi berhasil dimuat ulang!");
        }
    }

    /**
     * Menangani aksi tombol hapus riwayat transaksi.
     * Menampilkan konfirmasi sebelum menghapus semua data.
     */
    @FXML
    private void handleClearHistory() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Semua Riwayat Transaksi");
        alert.setContentText("Apakah Anda yakin ingin menghapus semua riwayat transaksi? Tindakan ini tidak dapat dibatalkan.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            mesinPenjual.clearTransactionHistory();
            loadTransactionData();
            updateStatistics();
            showInfo("Berhasil", "Semua riwayat transaksi telah dihapus!");
        }
    }

    /**
     * Menampilkan dialog error.
     *
     * @param title Judul dialog
     * @param message Pesan error yang akan ditampilkan
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog informasi.
     *
     * @param title Judul dialog
     * @param message Pesan informasi yang akan ditampilkan
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}