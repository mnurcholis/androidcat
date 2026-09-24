# Android CAT - Computer Assisted Test (Native Kotlin + Jetpack Compose)

Aplikasi mobile Android Native untuk simulasi ujian CAT BKN (SKD CPNS & Sekolah Kedinasan) yang terhubung langsung dengan backend NestJS cloud online 24/7 (`https://backendcat-u4ad.vercel.app/api/`).

---

## 🚀 Keunggulan Arsitektur Native

- **100% Native Kotlin + Jetpack Compose (Material 3)**: Tanpa webview lambat, tanpa mesin JS berat.
- **Super Ringan & Irit RAM**: Berjalan mulus 60–120 FPS di HP spesifikasi rendah (RAM 2GB–3GB).
- **Aman Selama Ujian**: Mencegah aplikasi force-close di tengah ujian 100 menit.
- **Role-Based Adaptation**: Otomatis menyesuaikan antarmuka sesuai role akun yang login (**Siswa / Peserta** atau **Administrator**).

---

## 📱 Fitur Utama

### 1. Mode Peserta (Siswa)
* **Simulasi Ujian CAT Resmi**:
  - Timer countdown presisi dengan indikator warna (warning & critical).
  - Soal pilihan ganda A, B, C, D, E dengan umpan balik sentuhan cepat.
  - Fitur penanda soal **"Ragu-ragu"**.
  - **Lembar Nomor Soal (Drawer Grid 110 Soal)**: Memudahkan lompat langsung ke butir soal manapun dengan kode warna (Hijau = Terjawab, Kuning = Ragu-ragu, Abu-abu = Belum).
  - Auto-submit otomatis jika waktu ujian habis.
* **Mode Belajar Mandiri**:
  - Latihan butir soal per kategori (TWK, TIU, TKP) tanpa tekanan waktu.
  - Kunci jawaban dan pembahasan muncul seketika setelah memilih opsi.
* **Hasil Skor & Passing Grade**:
  - Tampilan skor real-time dengan status kelulusan passing grade resmi BKN.
  - Pembahasan lengkap butir demi butir soal.
* **Modul & Materi Bacaan**:
  - Baca ringkasan materi SKD, rumus cepat TIU, dan tips TKP langsung dari ponsel.

### 2. Mode Administrator
* **Dashboard Statistik Real-time**:
  - Pantau total butir bank soal, total pengguna terdaftar, dan jumlah kategori.
* **AI Question Generator (Gemini)**:
  - Buat soal SKD berkualitas tinggi secara instan langsung dari HP menggunakan Gemini AI di backend.
  - Konfigurasi kategori, topik kisi-kisi, jumlah soal, dan tingkat kesulitan (Mudah, Sedang, HOTS).
* **Kelola Bank Soal**:
  - Review butir soal, filter kategori, dan hapus butir soal yang bermasalah.
* **Mode Siswa**:
  - Tombol beralih cepat untuk mencoba simulasi ujian dari sudut pandang peserta.

---

## 🛠️ Stack Teknologi

- **Bahasa**: Kotlin 1.9.23
- **UI Framework**: Jetpack Compose (BOM 2024.05.00) & Material 3
- **Navigation**: Jetpack Navigation Compose
- **Networking**: Retrofit 2.11.0 + OkHttp 4.12.0
- **Serialization**: Gson 2.11.0
- **State & Asynchronous**: Kotlin Coroutines & StateFlow
- **Session & Keystore**: Encrypted / SharedPreferences SessionManager

---

## 📂 Struktur Direktori

```text
androidcat/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── res/ (Icons, Colors, Themes, Strings)
│       └── java/com/cat/androidcat/
│           ├── CatApplication.kt
│           ├── MainActivity.kt
│           ├── data/
│           │   ├── api/ (ApiService, RetrofitClient, AuthInterceptor)
│           │   ├── model/ (Auth, Question, Exam, Category, Material models)
│           │   └── repository/ (AuthRepo, ExamRepo, MaterialRepo, AdminRepo, SessionManager)
│           ├── ui/
│           │   ├── theme/ (Color, Theme, Type)
│           │   ├── navigation/ (Screen, AppNavHost)
│           │   ├── components/ (CatButton, OptionItem, TimerBadge)
│           │   └── screens/
│           │       ├── auth/ (LoginScreen, RegisterScreen)
│           │       ├── user/ (HomeScreen, ExamScreen, ResultScreen, MaterialsScreen)
│           │       └── admin/ (AdminDashboardScreen, AdminAIGeneratorScreen, AdminQuestionsScreen)
│           └── viewmodel/ (AuthViewModel, ExamViewModel, MaterialViewModel, AdminViewModel)
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

---

## 💻 Cara Menjalankan Project

### 1. Buka di Android Studio
1. Buka Android Studio.
2. Pilih **File** ➔ **Open** ➔ Arahkan ke folder `/Users/macbook/Projects/uji/androidcat`.
3. Tunggu proses *Gradle Sync* selesai.
4. Klik tombol **Run (▶)** untuk menjalankan ke Emulator atau HP Android via USB Debugging.

### 2. Kompilasi APK via Terminal
Untuk membuat file installer `.apk` secara mandiri:
```bash
cd /Users/macbook/Projects/uji/androidcat

# Build Debug APK
./gradlew assembleDebug

# Lokasi File APK Hasil Build:
# app/build/outputs/apk/debug/app-debug.apk
```
