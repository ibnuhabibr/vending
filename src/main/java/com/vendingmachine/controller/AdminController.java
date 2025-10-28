package com.vendingmachine.controller;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import com.vendingmachine.model.Barang;
import com.vendingmachine.model.MesinPenjual;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

/**
 * Controller untuk panel administrasi vending machine.
 * Mengelola operasi CRUD (Create, Read, Update, Delete) untuk produk
 * dan menyediakan antarmuka untuk manajemen inventori.
 * 
 * Fitur yang dikelola:
 * - Tampilan tabel produk dengan informasi lengkap
 * - Form input untuk menambah/edit produk
 * - Validasi input data produk
 * - Konfirmasi untuk operasi update dan delete
 * - Refresh otomatis tabel setelah perubahan data
 * 
 * @author Tim Pengembang Vending Machine
 * @version 1.0
 * @since 2024
 */
public class AdminController {

    @FXML
    private TableView<Barang> productTable;

    @FXML
    private TableColumn<Barang, String> idColumn;

    @FXML
    private TableColumn<Barang, String> nameColumn;

    @FXML
    private TableColumn<Barang, Double> priceColumn;

    @FXML
    private TableColumn<Barang, Integer> stockColumn;

    @FXML
    private TableColumn<Barang, String> imagePathColumn;

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField stockField;

    @FXML
    private TextField imagePathField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button browseButton;

    @FXML
    private Button clearButton;

    private MesinPenjual mesinPenjual;
    private MainController mainController;
    private ObservableList<Barang> barangList;
    private NumberFormat currencyFormat;

    /**
     * Inisialisasi controller dan setup tabel.
     * Dipanggil otomatis setelah FXML dimuat.
     */
    @FXML
    public void initialize() {
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        // Konfigurasi kolom tabel untuk menampilkan data barang
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idBarang"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("hargaBarang"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stokSekarang"));
        imagePathColumn.setCellValueFactory(new PropertyValueFactory<>("pathGambar"));

        // Format kolom harga untuk menampilkan mata uang
        priceColumn.setCellFactory(column -> new TableCell<Barang, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatCurrency(item));
                }
            }
        });

        // Event listener untuk seleksi tabel - mengisi form saat item dipilih
        productTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    populateFields(newValue);
                }
            }
        );

        barangList = FXCollections.observableArrayList();
        productTable.setItems(barangList);
    }

    /**
     * Mengatur instance MesinPenjual dan memuat data ke tabel.
     *
     * @param mesinPenjual Instance MesinPenjual yang berisi data barang
     */
    public void setMesinPenjual(MesinPenjual mesinPenjual) {
        this.mesinPenjual = mesinPenjual;
        loadTableData();
    }

    /**
     * Mengatur referensi ke MainController untuk refresh tampilan.
     *
     * @param mainController Instance MainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Memuat data barang dari MesinPenjual ke tabel.
     */
    private void loadTableData() {
        barangList.clear();
        barangList.addAll(mesinPenjual.getDaftarBarang());
        productTable.refresh();
    }

    /**
     * Mengisi field input dengan data barang yang dipilih.
     *
     * @param barang Objek Barang yang datanya akan ditampilkan di field
     */
    private void populateFields(Barang barang) {
        idField.setText(barang.getIdBarang());
        nameField.setText(barang.getNamaBarang());
        priceField.setText(String.valueOf(barang.getHargaBarang()));
        stockField.setText(String.valueOf(barang.getStokSekarang()));
        imagePathField.setText(barang.getPathGambar());
    }

    /**
     * Menangani aksi tombol tambah barang baru.
     * Memvalidasi input dan menambahkan barang ke sistem.
     */
    @FXML
    private void handleAddButton() {
        try {
            // Validasi input
            if (!validateInput()) {
                return;
            }

            // Buat objek barang baru
            Barang barang = new Barang(
                idField.getText().trim(),
                nameField.getText().trim(),
                Double.parseDouble(priceField.getText().trim()),
                Integer.parseInt(stockField.getText().trim()),
                imagePathField.getText().trim()
            );

            // Tambahkan ke mesin penjual
            mesinPenjual.tambahBarang(barang);

            // Refresh tabel
            loadTableData();

            // Clear form
            clearFields();

            // Tampilkan pesan sukses
            showAlert(Alert.AlertType.INFORMATION, "Sukses",
                     "Barang berhasil ditambahkan!");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                     "Format harga atau stok tidak valid!");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Menangani aksi tombol update barang yang dipilih.
     * Memvalidasi input dan memperbarui data barang.
     */
    @FXML
    private void handleUpdateButton() {
        // Mendapatkan barang yang dipilih dari tabel
        Barang selectedBarang = productTable.getSelectionModel().getSelectedItem();

        if (selectedBarang == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan",
                     "Silakan pilih barang yang akan diupdate!");
            return;
        }

        try {
            // Validasi input sebelum melakukan update
            if (!validateInput()) {
                return;
            }

            // Konfirmasi dari user sebelum melakukan perubahan
            Optional<ButtonType> result = showConfirmation(
                "Konfirmasi Update",
                "Apakah Anda yakin ingin mengupdate barang ini?",
                "Data lama akan diganti dengan data baru."
            );

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Membuat objek barang baru dengan data dari form
                Barang dataBaru = new Barang(
                    idField.getText().trim(),
                    nameField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    Integer.parseInt(stockField.getText().trim()),
                    imagePathField.getText().trim()
                );

                // Melakukan update barang melalui MesinPenjual
                mesinPenjual.updateBarang(selectedBarang.getIdBarang(), dataBaru);

                // Refresh tabel untuk menampilkan data terbaru
                loadTableData();

                // Bersihkan form setelah update berhasil
                clearFields();

                // Tampilkan pesan sukses kepada user
                showAlert(Alert.AlertType.INFORMATION, "Sukses",
                         "Barang berhasil diupdate!");
            }

        } catch (NumberFormatException e) {
            // Handle error jika format angka tidak valid
            showAlert(Alert.AlertType.ERROR, "Error",
                     "Format harga atau stok tidak valid!");
        } catch (IllegalArgumentException e) {
            // Handle error dari validasi business logic
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Menangani aksi tombol hapus barang yang dipilih.
     * Menampilkan konfirmasi sebelum menghapus.
     */
    @FXML
    private void handleDeleteButton() {
        Barang selectedBarang = productTable.getSelectionModel().getSelectedItem();

        if (selectedBarang == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan",
                     "Silakan pilih barang yang akan dihapus!");
            return;
        }

        // Konfirmasi penghapusan
        Optional<ButtonType> result = showConfirmation(
            "Konfirmasi Hapus",
            "Apakah Anda yakin ingin menghapus barang ini?",
            "Barang: " + selectedBarang.getNamaBarang() + "\nAksi ini tidak dapat dibatalkan!"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Hapus barang
            boolean success = mesinPenjual.hapusBarang(selectedBarang.getIdBarang());

            if (success) {
                // Refresh tabel
                loadTableData();

                // Clear form
                clearFields();

                // Tampilkan pesan sukses
                showAlert(Alert.AlertType.INFORMATION, "Sukses",
                         "Barang berhasil dihapus!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                         "Gagal menghapus barang!");
            }
        }
    }

    /**
     * Menangani aksi tombol browse untuk memilih file gambar.
     * Membuka FileChooser untuk memilih gambar produk.
     */
    @FXML
    private void handleBrowseButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Produk");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());

        if (selectedFile != null) {
            // Atur path relatif untuk resources
            String relativePath = "/images/" + selectedFile.getName();
            imagePathField.setText(relativePath);

            showAlert(Alert.AlertType.INFORMATION, "Info",
                     "Pastikan file gambar disimpan di folder:\nsrc/main/resources/images/");
        }
    }

    /**
     * Menangani aksi tombol clear untuk mengosongkan semua field.
     */
    @FXML
    private void handleClearButton() {
        clearFields();
        productTable.getSelectionModel().clearSelection();
    }

    /**
     * Membersihkan semua field input.
     */
    private void clearFields() {
        idField.clear();
        nameField.clear();
        priceField.clear();
        stockField.clear();
        imagePathField.clear();
    }

    /**
     * Validasi input dari semua field.
     *
     * @return true jika semua input valid, false jika ada yang tidak valid
     */
    private boolean validateInput() {
        if (idField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "ID Barang tidak boleh kosong!");
            return false;
        }

        if (nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Nama Barang tidak boleh kosong!");
            return false;
        }

        if (priceField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Harga tidak boleh kosong!");
            return false;
        }

        if (stockField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Stok tidak boleh kosong!");
            return false;
        }

        if (imagePathField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Path Gambar tidak boleh kosong!");
            return false;
        }

        try {
            double harga = Double.parseDouble(priceField.getText().trim());
            if (harga < 0) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Harga tidak boleh negatif!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Harga harus berupa angka!");
            return false;
        }

        try {
            int stok = Integer.parseInt(stockField.getText().trim());
            if (stok < 0) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Stok tidak boleh negatif!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Stok harus berupa angka bulat!");
            return false;
        }

        return true;
    }

    /**
     * Memformat angka menjadi format mata uang Rupiah.
     *
     * @param amount Jumlah yang akan diformat
     * @return String dengan format Rupiah
     */
    private String formatCurrency(double amount) {
        return currencyFormat.format(amount).replace("IDR", "Rp");
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
}

