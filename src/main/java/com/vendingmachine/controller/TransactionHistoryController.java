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
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller untuk mengelola tampilan riwayat transaksi.
 * Menampilkan daftar semua transaksi yang telah dilakukan dalam bentuk tabel.
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        currencyFormat.setMaximumFractionDigits(0);
        
        // Setup table columns
        setupTableColumns();
        
        // Initialize transaction data
        transactionData = FXCollections.observableArrayList();
        transactionTable.setItems(transactionData);
        
        // Setup table selection listener for double-click to view details
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
     * Setup kolom-kolom tabel transaksi.
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
        
        // Set custom cell factory for status column to add styling
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
     * Set referensi ke MesinPenjual dan load data transaksi.
     */
    public void setMesinPenjual(MesinPenjual mesinPenjual) {
        this.mesinPenjual = mesinPenjual;
        loadTransactionData();
        updateStatistics();
    }

    /**
     * Load data transaksi ke dalam tabel.
     */
    private void loadTransactionData() {
        if (mesinPenjual != null) {
            transactionData.clear();
            transactionData.addAll(mesinPenjual.getDaftarTransaksi());
        }
    }

    /**
     * Update statistik transaksi.
     */
    private void updateStatistics() {
        if (mesinPenjual == null) return;
        
        var transactions = mesinPenjual.getDaftarTransaksi();
        int totalTransactions = transactions.size();
        
        double totalRevenue = transactions.stream()
            .filter(t -> t.getStatus() == Transaksi.StatusTransaksi.BERHASIL)
            .mapToDouble(Transaksi::getTotalHarga)
            .sum();
        
        long successfulTransactions = transactions.stream()
            .filter(t -> t.getStatus() == Transaksi.StatusTransaksi.BERHASIL)
            .count();
        
        totalTransactionsLabel.setText(String.valueOf(totalTransactions));
        totalRevenueLabel.setText(formatCurrency(totalRevenue));
        successfulTransactionsLabel.setText(String.valueOf(successfulTransactions));
    }

    /**
     * Format mata uang Indonesia.
     */
    private String formatCurrency(double amount) {
        return String.format("Rp %.0f", amount);
    }

    /**
     * Menampilkan detail transaksi dalam dialog.
     */
    private void showTransactionDetail(Transaksi transaksi) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detail Transaksi");
        alert.setHeaderText("Informasi Lengkap Transaksi");
        alert.setContentText(transaksi.getDetailTransaksi());
        
        // Style the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("transaction-detail-dialog");
        
        alert.showAndWait();
    }

    /**
     * Handle tombol kembali ke main view.
     */
    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vendingmachine/MainView.fxml"));
            Parent root = loader.load();
            
            MainController mainController = loader.getController();
            mainController.setMesinPenjual(mesinPenjual);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("PENShop - Vending Machine");
        } catch (IOException e) {
            showError("Error", "Gagal kembali ke halaman utama: " + e.getMessage());
        }
    }

    /**
     * Handle refresh data transaksi.
     */
    @FXML
    private void handleRefresh() {
        loadTransactionData();
        updateStatistics();
        showInfo("Refresh", "Data transaksi berhasil diperbarui!");
    }

    /**
     * Handle hapus semua riwayat transaksi.
     */
    @FXML
    private void handleClearHistory() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Semua Riwayat Transaksi");
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus semua riwayat transaksi? Tindakan ini tidak dapat dibatalkan.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            mesinPenjual.clearTransactionHistory();
            loadTransactionData();
            updateStatistics();
            showInfo("Berhasil", "Semua riwayat transaksi telah dihapus!");
        }
    }

    /**
     * Menampilkan dialog error.
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
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}