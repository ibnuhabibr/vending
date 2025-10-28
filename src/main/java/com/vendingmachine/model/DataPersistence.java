package com.vendingmachine.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas utilitas untuk mengelola persistensi data aplikasi vending machine.
 * Menyediakan fungsi untuk menyimpan dan memuat data produk menggunakan
 * serialisasi Java ke file sistem.
 * 
 * Fitur yang disediakan:
 * - Penyimpanan data produk ke file
 * - Pemuatan data produk dari file
 * - Penghapusan file data
 * - Pengecekan keberadaan file data
 * - Manajemen direktori penyimpanan otomatis
 * 
 * @author Tim Pengembang Vending Machine
 * @version 2.0
 * @since 2024
 */
public class DataPersistence {

    private static final String DATA_FILE = "vending_data.dat";
    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + ".vending_machine";

    /**
     * Menyimpan daftar barang ke file.
     *
     * @param daftarBarang List barang yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public static boolean saveData(List<Barang> daftarBarang) {
        try {
            // Membuat direktori penyimpanan jika belum ada
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs(); // Buat direktori beserta parent directories
            }

            File file = new File(DATA_DIR + File.separator + DATA_FILE);

            // Menggunakan try-with-resources untuk auto-close stream
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(file))) {
                // Simpan copy dari list untuk menghindari reference issues
                oos.writeObject(new ArrayList<>(daftarBarang));
                System.out.println("Data berhasil disimpan ke: " + file.getAbsolutePath());
                return true;
            }
        } catch (IOException e) {
            // Log error untuk debugging
            System.err.println("Error saat menyimpan data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Memuat daftar barang dari file.
     *
     * @return List barang yang dimuat, atau list kosong jika file tidak ada
     */
    @SuppressWarnings("unchecked")
    public static List<Barang> loadData() {
        File file = new File(DATA_DIR + File.separator + DATA_FILE);

        // Periksa apakah file data ada sebelum mencoba membaca
        if (!file.exists()) {
            System.out.println("File data tidak ditemukan. Menggunakan data default.");
            return new ArrayList<>();
        }

        // Menggunakan try-with-resources untuk auto-close stream
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            // Cast object yang dibaca menjadi List<Barang>
            List<Barang> daftarBarang = (List<Barang>) ois.readObject();
            System.out.println("Data berhasil dimuat: " + daftarBarang.size() + " produk");
            return daftarBarang;
        } catch (IOException | ClassNotFoundException e) {
            // Handle error saat membaca file atau class tidak ditemukan
            System.err.println("Error saat memuat data: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Return list kosong jika gagal
        }
    }

    /**
     * Menghapus file data.
     *
     * @return true jika berhasil dihapus, false jika gagal
     */
    public static boolean deleteData() {
        File file = new File(DATA_DIR + File.separator + DATA_FILE);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Mengecek apakah file data ada.
     *
     * @return true jika file data ada, false jika tidak
     */
    public static boolean dataExists() {
        File file = new File(DATA_DIR + File.separator + DATA_FILE);
        return file.exists();
    }
}