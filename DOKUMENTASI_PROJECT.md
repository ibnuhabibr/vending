# 📱 PENShop - Aplikasi Vending Machine

## 🎯 Latar Belakang Project

**PENShop** adalah aplikasi vending machine modern yang dikembangkan menggunakan **Java 17** dan **JavaFX 21**. Project ini merupakan implementasi sistem penjualan otomatis yang mensimulasikan mesin vending machine dengan antarmuka grafis yang user-friendly dan sistem manajemen produk yang komprehensif.

### 🎓 Informasi Project
- **Nama Project**: PENShop Vending Machine
- **Kelas**: 2 D3 IT B
- **Teknologi**: Java 17, JavaFX 21, Maven
- **Versi**: 1.0.0
- **Arsitektur**: Model-View-Controller (MVC)

---

## 🏗️ Arsitektur Sistem

### 📊 Class Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐
│    MainApp      │    │  MainController │    │ AdminController │    │TransactionHistoryController│
│                 │    │                 │    │                 │    │                         │
│ - mesinPenjual  │───▶│ - mesinPenjual  │    │ - mesinPenjual  │    │ - mesinPenjual          │
│ + start()       │    │ - productTilePane│    │ - productTable  │    │ - transactionTable      │
│ + main()        │    │ + setMesinPenjual│    │ + setMesinPenjual│    │ + setMesinPenjual       │
└─────────────────┘    │ + loadProducts()│    │ + handleAddButton│    │ + loadTransactionData() │
                       │ + handleBuyButton│    │ + handleUpdate() │    │ + handleRefresh()       │
                       │ + handleHistory()│    │ + handleDelete() │    │ + handleClearHistory()  │
                       └─────────────────┘    └─────────────────┘    └─────────────────────────┘
                                ▲                       ▲                           ▲
                                │                       │                           │
                                ▼                       ▼                           ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐
│  MesinPenjual   │    │     Barang      │    │   Transaksi     │    │  TransactionDetailView  │
│                 │    │                 │    │                 │    │                         │
│ - daftarBarang  │───▶│ - idBarang      │    │ - idTransaksi   │    │ + showTransactionDetail │
│ - riwayatTransaksi   │ - namaBarang    │    │ - barangYangDibeli    │ + displayTransactionInfo│
│ + tambahBarang()│    │ - hargaBarang   │    │ - kuantitas     │    └─────────────────────────┘
│ + hapusBarang() │    │ - stokSekarang  │    │ - totalHarga    │
│ + cariBarang()  │    │ - pathGambar    │    │ - status        │
│ + prosesPembelian()  │ + kurangiStok() │    │ - waktuTransaksi│
│ + getStatistik()│    └─────────────────┘    │ + getDetailTransaksi()
└─────────────────┘                           └─────────────────┘
         ▲
         │
         ▼
┌─────────────────┐
│ DataPersistence │
│                 │
│ + saveData()    │
│ + loadData()    │
│ + deleteData()  │
│ + dataExists()  │
└─────────────────┘
```

---

## 📦 Struktur Package dan Class

### 🎯 Package: `com.vendingmachine`

#### 1. **MainApp.java** - Entry Point Aplikasi
- **Fungsi**: Kelas utama yang menjalankan aplikasi JavaFX
- **Tanggung Jawab**:
  - Inisialisasi aplikasi
  - Load data awal produk
  - Setup scene dan stage
  - Konfigurasi FXML loader

#### 2. **Package: `com.vendingmachine.model`** - Business Logic Layer

##### 🏷️ **Barang.java** - Entity Class
```java
public class Barang implements Serializable {
    - String idBarang
    - String namaBarang  
    - double hargaBarang
    - int stokSekarang
    - String pathGambar
}
```
- **Fungsi**: Merepresentasikan produk dalam vending machine
- **Fitur**: Enkapsulasi data, validasi stok, serializable untuk persistence

##### 🏪 **MesinPenjual.java** - Core Business Logic
```java
public class MesinPenjual {
    - List<Barang> daftarBarang
    - List<Transaksi> riwayatTransaksi
    
    + tambahBarang(Barang)
    + hapusBarang(String)
    + updateBarang(String, Barang)
    + cariBarang(String)
    + prosesPembelian(Barang)
}
```
- **Fungsi**: Mengelola inventaris dan transaksi
- **Fitur**: CRUD operations, validasi bisnis, auto-save data

##### 🧾 **Transaksi.java** - Transaction Record
```java
public class Transaksi {
    - String idTransaksi
    - Barang barangYangDibeli
    - LocalDateTime waktuTransaksi
    
    + generateIdTransaksi()
    + getDetailTransaksi()
}
```
- **Fungsi**: Mencatat setiap transaksi pembelian
- **Fitur**: Auto-generate ID, timestamp, detail formatting

##### 💾 **DataPersistence.java** - Data Management
```java
public class DataPersistence {
    + static saveData(List<Barang>)
    + static loadData()
    + static deleteData()
    + static dataExists()
}
```
- **Fungsi**: Mengelola penyimpanan data persisten
- **Fitur**: Serialization, file I/O, error handling

#### 3. **Package: `com.vendingmachine.controller`** - Presentation Layer

##### 🛒 **MainController.java** - Customer Interface
- **Fungsi**: Mengontrol tampilan pembeli
- **Fitur**:
  - Display produk dalam grid layout
  - Handle pembelian dengan QR Code simulation
  - Payment verification flow
  - Real-time stock updates

##### ⚙️ **AdminController.java** - Admin Interface  
- **Fungsi**: Mengontrol panel admin
- **Fitur**:
  - CRUD operations untuk produk
  - Table view dengan sorting
  - Form validation
  - File browser untuk gambar
  - Bulk operations

##### 📊 **TransactionHistoryController.java** - Transaction Management
- **Fungsi**: Mengontrol tampilan riwayat transaksi
- **Fitur**:
  - Display riwayat transaksi dalam table view
  - Statistik transaksi (total, revenue, success rate)
  - Filter dan refresh data transaksi
  - Detail view untuk setiap transaksi
  - Clear history functionality
  - Export dan analisis data transaksi

---

## 🎨 User Interface Design

### 🖥️ Main View (Customer Interface)
- **Layout**: BorderPane dengan TilePane untuk produk
- **Komponen**:
  - Header dengan logo "PENShop"
  - Product grid dengan kartu produk
  - Admin button untuk akses panel admin
  - Footer dengan informasi kelas

### ⚙️ Admin View (Management Interface)
- **Layout**: BorderPane dengan TableView dan Form
- **Komponen**:
  - Product table dengan kolom: ID, Nama, Harga, Stok, Path Gambar
  - Form input untuk CRUD operations
  - Action buttons: Add, Update, Delete, Browse, Clear
  - File chooser untuk upload gambar

### 📊 Transaction History View (Transaction Management)
- **Layout**: BorderPane dengan TableView dan Statistics Panel
- **Komponen**:
  - Header dengan judul "Riwayat Transaksi" dan tombol kembali
  - Statistics panel: Total transaksi, Total revenue, Transaksi berhasil
  - Transaction table dengan kolom: ID, Barang, Harga, Kuantitas, Total, Status, Tanggal & Waktu
  - Action buttons: Refresh, Hapus Semua Riwayat
  - Double-click untuk detail transaksi

### 🎨 Styling (CSS)
- **Theme**: Modern dark gradient dengan accent blue
- **Features**:
  - Smooth animations dan transitions
  - Hover effects pada buttons dan cards
  - Responsive design
  - Professional color scheme
  - Custom fonts dan typography

---

## 🔧 Teknologi dan Dependencies

### 📋 Maven Configuration (pom.xml)
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <javafx.version>21.0.1</javafx.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.1</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21.0.1</version>
    </dependency>
</dependencies>
```

### 🏃‍♂️ Build & Run
- **Build Tool**: Apache Maven 3.x
- **Run Script**: `run.bat` untuk Windows
- **Main Class**: `com.vendingmachine.MainApp`
- **Module System**: Java 9+ modules dengan `module-info.java`

---

## 🚀 Fitur Utama

### 👥 Untuk Customer:
1. **Browse Produk**: Melihat semua produk dalam grid layout
2. **Informasi Produk**: Gambar, nama, harga, dan stok
3. **Pembelian**: Simulasi pembayaran dengan QR Code
4. **Konfirmasi**: Verifikasi pembayaran dan konfirmasi pembelian
5. **Real-time Update**: Stok terupdate otomatis setelah pembelian
6. **Riwayat Transaksi**: Akses ke history pembelian dengan statistik lengkap

### 🔧 Untuk Admin:
1. **Manajemen Produk**: CRUD lengkap untuk produk
2. **Upload Gambar**: File browser untuk memilih gambar produk
3. **Validasi Data**: Form validation untuk input yang benar
4. **Table Management**: Sorting dan selection pada table
5. **Data Persistence**: Auto-save setiap perubahan

### 💾 Data Management:
1. **Persistent Storage**: Data tersimpan otomatis ke file
2. **Auto-load**: Data dimuat otomatis saat aplikasi start
3. **Error Handling**: Robust error handling untuk I/O operations
4. **Data Validation**: Validasi data pada level model dan controller
5. **Transaction History**: Pencatatan dan penyimpanan riwayat transaksi
6. **Statistics Calculation**: Kalkulasi otomatis statistik penjualan

---

## 📁 Struktur File Project

```
vending_machine/
├── pom.xml                          # Maven configuration
├── run.bat                          # Windows launcher script
├── DOKUMENTASI_PROJECT.md           # Project documentation
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java     # Java module definition
│       │   └── com/vendingmachine/
│       │       ├── MainApp.java     # Application entry point
│       │       ├── controller/
│       │       │   ├── MainController.java              # Customer UI controller
│       │       │   ├── AdminController.java             # Admin UI controller
│       │       │   └── TransactionHistoryController.java # Transaction history controller
│       │       └── model/
│       │           ├── Barang.java            # Product entity
│       │           ├── MesinPenjual.java      # Business logic
│       │           ├── Transaksi.java         # Transaction record
│       │           └── DataPersistence.java   # Data management
│       └── resources/
│           ├── com/vendingmachine/
│           │   ├── MainView.fxml              # Customer UI layout
│           │   ├── AdminView.fxml             # Admin UI layout
│           │   ├── TransactionHistoryView.fxml # Transaction history UI layout
│           │   └── TransactionDetailView.fxml  # Transaction detail UI layout
│           ├── images/               # Product images (18 files)
│           │   ├── air.png, cola.png, kopi.png, etc.
│           │   └── qris_payment.png  # Payment QR code
│           └── styles.css            # Application styling
└── target/                          # Compiled classes (Maven output)
```

---

## 🎯 Design Patterns yang Digunakan

### 1. **Model-View-Controller (MVC)**
- **Model**: Package `model` dengan business logic
- **View**: FXML files untuk UI layout
- **Controller**: Package `controller` untuk UI logic

### 2. **Singleton Pattern**
- `DataPersistence` menggunakan static methods untuk global access

### 3. **Observer Pattern**
- JavaFX properties untuk data binding dan UI updates

### 4. **Factory Pattern**
- Dynamic creation of product cards dalam `MainController`

---

## 🔒 Keamanan dan Validasi

### Input Validation:
- ✅ Validasi format harga (numeric)
- ✅ Validasi stok (positive integer)
- ✅ Validasi ID unik untuk produk
- ✅ Validasi path gambar exists

### Error Handling:
- ✅ Try-catch untuk file I/O operations
- ✅ User-friendly error messages
- ✅ Graceful degradation jika file tidak ditemukan
- ✅ Validation feedback dalam UI

---

## 📈 Performa dan Optimasi

### Memory Management:
- Efficient use of JavaFX collections
- Proper resource disposal
- Image caching untuk performa

### User Experience:
- Smooth animations dan transitions
- Responsive UI design
- Intuitive navigation flow
- Real-time feedback

---

## 🔧 Troubleshooting dan Perbaikan

### ❌ Masalah yang Pernah Terjadi dan Solusinya:

#### 1. **FXML LoadException pada TransactionHistoryView**
- **Masalah**: `javafx.fxml.LoadException` saat membuka riwayat transaksi
- **Penyebab**: 
  - Ketidakcocokan tipe data antara FXML dan Controller
  - Karakter `&` tidak di-escape dengan benar dalam XML
- **Solusi**:
  - Mengubah `TableColumn<Transaksi, Integer>` menjadi `TableColumn<Transaksi, String>`
  - Memperbaiki XML entity: `&` → `&amp;` dalam teks "Tanggal & Waktu"
  - Menambahkan proper cell value factory untuk semua kolom

#### 2. **XML Parsing Error**
- **Masalah**: `XMLStreamException` pada baris 52 FXML
- **Penyebab**: Karakter `&` dalam atribut text tidak di-escape
- **Solusi**: Menggunakan XML entity `&amp;` untuk karakter ampersand

#### 3. **TableColumn Type Mismatch**
- **Masalah**: Inconsistency antara deklarasi controller dan FXML
- **Penyebab**: Mixed data types dalam TableColumn generics
- **Solusi**: Standardisasi semua TableColumn menggunakan String type

### 🛠️ Best Practices yang Diterapkan:
- ✅ Consistent data type usage dalam JavaFX TableView
- ✅ Proper XML entity escaping dalam FXML files
- ✅ Comprehensive error handling dan debugging
- ✅ Clean compile dan restart untuk memastikan perubahan terimplementasi
- ✅ Modular controller design untuk separation of concerns

---

## 🎓 Kesimpulan

**PENShop Vending Machine** adalah implementasi lengkap sistem vending machine dengan:

✨ **Kelebihan**:
- Arsitektur MVC yang clean dan maintainable
- UI modern dengan JavaFX dan CSS styling
- Data persistence yang robust
- Comprehensive error handling
- User-friendly untuk customer dan admin
- Modular design dengan separation of concerns
- **Transaction history management** dengan statistik lengkap
- **Real-time data updates** dan refresh functionality
- **Robust XML parsing** dan proper entity handling

🎯 **Pembelajaran**:
- Object-Oriented Programming principles
- JavaFX GUI development dengan TableView dan complex layouts
- File I/O dan serialization untuk data persistence
- MVC architecture implementation dengan multiple controllers
- Maven build management dan dependency handling
- Modern UI/UX design dengan responsive layouts
- **XML/FXML best practices** dan proper entity escaping
- **JavaFX data binding** dan ObservableList management
- **Error handling dan debugging** untuk production-ready applications
- **Modular design patterns** untuk scalable applications

🚀 **Fitur Unggulan**:
- **Multi-view navigation** dengan seamless transitions
- **Transaction analytics** dengan real-time statistics
- **Data integrity** dengan comprehensive validation
- **Professional UI/UX** dengan modern design principles
- **Scalable architecture** untuk future enhancements

Project ini mendemonstrasikan kemampuan dalam mengembangkan aplikasi desktop Java yang professional dengan fitur lengkap, error handling yang robust, dan user experience yang excellent.

---

**Dibuat oleh: 2 D3 IT B**  
**Teknologi: Java 17 + JavaFX 21 + Maven**  
**Versi: 1.0.0**