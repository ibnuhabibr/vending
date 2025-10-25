@echo off
REM ============================================
REM Launcher untuk PENShop Vending Machine
REM ============================================

title PENShop - Vending Machine

echo ============================================
echo   PENShop Vending Machine
echo   Memulai aplikasi...
echo ============================================
echo.

REM Pindah ke direktori script
cd /d "%~dp0"

REM Jalankan aplikasi menggunakan Maven
call mvn clean javafx:run

REM Jika gagal, tampilkan pesan
if errorlevel 1 (
    echo.
    echo ============================================
    echo   ERROR: Gagal menjalankan aplikasi!
    echo   Pastikan Maven sudah terinstall.
    echo ============================================
    echo.
    pause
)
