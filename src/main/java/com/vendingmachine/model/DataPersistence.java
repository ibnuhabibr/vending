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
 * Kelas untuk mengelola penyimpanan dan pembacaan data produk dari file.
 * Menggunakan serialization untuk menyimpan data secara persisten.
 *
 * @author Senior Java Developer
 * @version 2.0
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
            // Buat direktori jika belum ada
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(DATA_DIR + File.separator + DATA_FILE);

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(file))) {
                oos.writeObject(new ArrayList<>(daftarBarang));
                System.out.println("Data berhasil disimpan ke: " + file.getAbsolutePath());
                return true;
            }
        } catch (IOException e) {
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

        if (!file.exists()) {
            System.out.println("File data tidak ditemukan. Menggunakan data default.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            List<Barang> daftarBarang = (List<Barang>) ois.readObject();
            System.out.println("Data berhasil dimuat: " + daftarBarang.size() + " produk");
            return daftarBarang;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error saat memuat data: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
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
