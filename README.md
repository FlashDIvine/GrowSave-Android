<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Language" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-brightgreen?style=for-the-badge" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-blue?style=for-the-badge" alt="Target SDK" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge" alt="Architecture" />
</p>

# 💰 GrowSave Android

> **Aplikasi manajemen keuangan dan tabungan berbasis Android** yang dirancang untuk mempermudah pengelolaan tagihan, transaksi keuangan, dan informasi pengumuman. Dilengkapi dengan integrasi pembayaran **Midtrans Snap** dan sistem autentikasi berbasis **JWT Token**.

---

## 📱 Preview

**GrowSave** adalah aplikasi Android native yang dibangun menggunakan **Kotlin** dengan arsitektur **MVVM (Model-View-ViewModel)**. Aplikasi ini menyediakan fitur lengkap mulai dari autentikasi pengguna, manajemen transaksi keuangan, pembayaran tagihan secara online melalui payment gateway Midtrans, hingga sistem pengumuman. Aplikasi terhubung ke backend REST API dan menggunakan JWT Token untuk keamanan komunikasi data.

---

## ✨ Features

| Fitur | Status | Deskripsi |
|:------|:------:|:----------|
| 🔐 Login | ✅ | Autentikasi pengguna dengan email & password |
| 📝 Register | ✅ | Pendaftaran akun baru dengan validasi input |
| 🔄 Auto-Login | ✅ | Sesi otomatis jika user sudah pernah login |
| 🏠 Home Dashboard | ✅ | Halaman utama aplikasi |
| 💸 Daftar Transaksi | ✅ | Menampilkan riwayat transaksi (income & expense) |
| 🧾 Daftar Tagihan | ✅ | Menampilkan tagihan dengan status paid/unpaid |
| 💳 Pembayaran Online | ✅ | Integrasi Midtrans Snap via WebView |
| 📢 Pengumuman | ✅ | Daftar pengumuman dengan dukungan gambar |
| 👤 Profil Pengguna | ✅ | Menampilkan informasi user yang sedang login |
| 🚪 Logout | ✅ | Hapus sesi dan redirect ke halaman login |
| 💱 Format Rupiah | ✅ | Format mata uang Indonesia (Rp) otomatis |
| 🎨 Material Design 3 | ✅ | UI modern dengan tema Material3 DayNight |
| 🔑 Role-Based User | ✅ | Mendukung role `admin` dan `user` |

---

## 🛠️ Tech Stack

| Teknologi | Detail |
|:----------|:-------|
| **Bahasa** | Kotlin 2.0.21 |
| **Platform** | Android (Min SDK 24 / Android 7.0) |
| **Target SDK** | 36 |
| **Build System** | Gradle Kotlin DSL + Version Catalog |
| **AGP** | 8.13.2 |
| **UI Toolkit** | Android XML Layouts + ViewBinding |
| **Tema** | Material Design 3 (DayNight NoActionBar) |
| **Networking** | Retrofit 2.11.0 + OkHttp 4.12.0 |
| **JSON Parser** | Gson Converter 2.11.0 |
| **Image Loading** | Glide 4.16.0 |
| **Async** | Kotlin Coroutines (viewModelScope) |
| **State Management** | LiveData + ViewModel |
| **Local Storage** | SharedPreferences (SessionManager) |
| **Payment Gateway** | Midtrans Snap (Sandbox WebView) |
| **JVM Target** | Java 11 |

---

## 🏗️ Architecture

Aplikasi ini menggunakan pola arsitektur **MVVM (Model-View-ViewModel)** yang dikombinasikan dengan **Repository Pattern** untuk memisahkan logika bisnis dari lapisan UI.

```
┌─────────────────────────────────────────────────────────┐
│                        VIEW                             │
│  (Activity / Fragment)                                  │
│  ┌─────────────┐ ┌─────────────┐ ┌──────────────────┐  │
│  │LoginActivity │ │MainActivity │ │PaymentActivity   │  │
│  └──────┬──────┘ └──────┬──────┘ └────────┬─────────┘  │
│         │               │                 │             │
│  ┌──────┴──────────┐ ┌──┴───┐ ┌───────────┘             │
│  │RegisterActivity │ │Frag- │ │                         │
│  └─────────────────┘ │ments │ │                         │
│                      └──┬───┘ │                         │
├─────────────────────────┼─────┼─────────────────────────┤
│                    VIEWMODEL                            │
│  ┌──────────────┐ ┌─────────────┐ ┌──────────────────┐  │
│  │AuthViewModel  │ │BillViewModel│ │TransactionVM     │  │
│  └──────┬───────┘ └──────┬──────┘ └────────┬─────────┘  │
│  ┌──────┴───────────┐ ┌──┴──────────────┐              │
│  │AnnouncementVM    │ │PaymentViewModel │              │
│  └──────┬───────────┘ └──┬──────────────┘              │
├─────────┼────────────────┼──────────────────────────────┤
│                    REPOSITORY                           │
│  ┌──────────────┐ ┌─────────────┐ ┌──────────────────┐  │
│  │AuthRepository │ │BillRepo     │ │TransactionRepo   │  │
│  └──────┬───────┘ └──────┬──────┘ └────────┬─────────┘  │
│  ┌──────┴───────────┐ ┌──┴──────────────┐              │
│  │AnnouncementRepo  │ │PaymentRepo      │              │
│  └──────┬───────────┘ └──┬──────────────┘              │
├─────────┼────────────────┼──────────────────────────────┤
│                    DATA SOURCE                          │
│           ┌──────────────────────┐                      │
│           │  RetrofitClient      │                      │
│           │  (Singleton)         │                      │
│           └──────────┬───────────┘                      │
│           ┌──────────┴───────────┐                      │
│           │    ApiService        │                      │
│           │  (REST API Interface)│                      │
│           └──────────────────────┘                      │
└─────────────────────────────────────────────────────────┘
```

### Alur Data:
```
User Action → View → ViewModel → Repository → ApiService (Retrofit) → Backend API
                ↑                                                          │
                └──────────── LiveData (Observe) ──────────────────────────┘
```

---

## 📂 Project Structure

```
GrowSave/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smk/growsave/
│   │   │   │   │
│   │   │   │   ├── 📱 LoginActivity.kt          # Halaman login (Launcher)
│   │   │   │   ├── 📱 MainActivity.kt           # Host activity + Bottom Navigation
│   │   │   │   ├── 📱 PaymentActivity.kt        # WebView Midtrans Snap
│   │   │   │   ├── 📱 RegisterActivity.kt       # Halaman registrasi
│   │   │   │   │
│   │   │   │   ├── adapter/                      # RecyclerView Adapters
│   │   │   │   │   ├── AnnouncementAdapter.kt    # Adapter pengumuman + Glide
│   │   │   │   │   ├── BillAdapter.kt            # Adapter tagihan + click listener
│   │   │   │   │   └── TransactionAdapter.kt     # Adapter transaksi + format Rupiah
│   │   │   │   │
│   │   │   │   ├── fragment/                     # UI Fragments
│   │   │   │   │   ├── HomeFragment.kt           # Halaman utama
│   │   │   │   │   ├── TransactionFragment.kt    # Daftar transaksi
│   │   │   │   │   ├── AnnouncementFragment.kt   # Daftar pengumuman
│   │   │   │   │   ├── BillsFragment.kt          # Daftar tagihan + pembayaran
│   │   │   │   │   └── ProfileFragment.kt        # Profil user + logout
│   │   │   │   │
│   │   │   │   ├── model/                        # Data Classes
│   │   │   │   │   ├── BaseResponse.kt           # Generic API response wrapper
│   │   │   │   │   ├── User.kt                   # Model user + enum UserRole
│   │   │   │   │   ├── Bill.kt                   # Model tagihan
│   │   │   │   │   ├── Transaction.kt            # Model transaksi
│   │   │   │   │   ├── Announcement.kt           # Model pengumuman
│   │   │   │   │   ├── PaymentRequest.kt         # Request body pembayaran
│   │   │   │   │   ├── PaymentResponse.kt        # Response snap token
│   │   │   │   │   └── auth/                     # Auth-specific models
│   │   │   │   │       ├── LoginRequest.kt       # Request body login
│   │   │   │   │       ├── LoginResponse.kt      # Response login (token + user)
│   │   │   │   │       └── RegisterRequest.kt    # Request body register
│   │   │   │   │
│   │   │   │   ├── network/                      # Networking Layer
│   │   │   │   │   ├── ApiService.kt             # Retrofit API endpoints interface
│   │   │   │   │   └── RetrofitClient.kt         # Singleton Retrofit + OkHttp config
│   │   │   │   │
│   │   │   │   ├── repository/                   # Repository Layer
│   │   │   │   │   ├── AuthRepository.kt         # Login & Register
│   │   │   │   │   ├── BillRepository.kt         # Tagihan
│   │   │   │   │   ├── TransactionRepository.kt  # Transaksi
│   │   │   │   │   ├── AnnouncementRepository.kt # Pengumuman
│   │   │   │   │   └── PaymentRepository.kt      # Pembayaran
│   │   │   │   │
│   │   │   │   ├── utils/                        # Utility Classes
│   │   │   │   │   └── SessionManager.kt         # JWT token & session (SharedPrefs)
│   │   │   │   │
│   │   │   │   └── viewmodel/                    # ViewModels
│   │   │   │       ├── AuthViewModel.kt          # Login & Register state
│   │   │   │       ├── BillViewModel.kt          # Tagihan state
│   │   │   │       ├── TransactionViewModel.kt   # Transaksi state
│   │   │   │       ├── AnnouncementViewModel.kt  # Pengumuman state
│   │   │   │       └── PaymentViewModel.kt       # Snap token state
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/                       # XML Layouts
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_payment.xml
│   │   │   │   │   ├── activity_register.xml
│   │   │   │   │   ├── fragment_home.xml
│   │   │   │   │   ├── fragment_transaction.xml
│   │   │   │   │   ├── fragment_announcement.xml
│   │   │   │   │   ├── fragment_bills.xml
│   │   │   │   │   ├── fragment_profile.xml
│   │   │   │   │   ├── item_transaction.xml      # RecyclerView item
│   │   │   │   │   ├── item_bill.xml             # RecyclerView item
│   │   │   │   │   └── item_announcement.xml     # RecyclerView item
│   │   │   │   ├── menu/
│   │   │   │   │   └── bottom_menu.xml           # Bottom Navigation menu
│   │   │   │   ├── drawable/                     # Vector icons & drawables
│   │   │   │   ├── values/                       # Colors, strings, themes
│   │   │   │   └── values-night/                 # Dark mode theme overrides
│   │   │   │
│   │   │   └── AndroidManifest.xml               # App manifest & permissions
│   │   │
│   │   └── build.gradle.kts                      # App-level dependencies
│   │
├── gradle/
│   └── libs.versions.toml                        # Version Catalog
├── build.gradle.kts                              # Root build config
├── settings.gradle.kts                           # Project settings
└── gradle.properties                             # Gradle configuration
```

---

## ⚙️ Installation

### Prerequisites
- **Android Studio** Ladybug atau versi terbaru
- **JDK 11** atau lebih tinggi
- **Android SDK** dengan API Level 36
- **Backend server** yang berjalan (Laravel / API yang kompatibel)

### Langkah Instalasi

1. **Clone repository**
   ```bash
   git clone https://github.com/FlashDIvine/GrowSave-Android.git
   ```

2. **Buka project di Android Studio**
   ```
   File → Open → Pilih folder GrowSave
   ```

3. **Sync Gradle**
   - Tunggu Android Studio menyelesaikan proses Gradle sync
   - Pastikan semua dependency terdownload

4. **Konfigurasi Base URL** (lihat bagian [API Configuration](#-api-configuration))

5. **Jalankan aplikasi**
   - Pilih emulator atau perangkat fisik
   - Tekan tombol **Run ▶️** atau `Shift + F10`

> **⚠️ Catatan:** Pastikan backend API sudah berjalan sebelum menjalankan aplikasi agar fitur autentikasi dan data dapat berfungsi.

---

## 🌐 API Configuration

Aplikasi berkomunikasi dengan backend REST API. Base URL dikonfigurasi di file:

📄 **`network/RetrofitClient.kt`**

```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/"
```

> `10.0.2.2` adalah alias untuk `localhost` pada Android Emulator. Jika menggunakan perangkat fisik, ganti dengan IP address komputer di jaringan lokal.

### API Endpoints

| Method | Endpoint | Auth | Deskripsi |
|:------:|:---------|:----:|:----------|
| `POST` | `/api/login` | ❌ | Login user |
| `POST` | `/api/register` | ❌ | Register user baru |
| `GET` | `/api/transactions` | 🔐 Bearer | Daftar transaksi |
| `GET` | `/api/announcements` | 🔐 Bearer | Daftar pengumuman |
| `GET` | `/api/bills` | 🔐 Bearer | Daftar tagihan |
| `POST` | `/api/payments` | 🔐 Bearer | Buat snap token pembayaran |

### Contoh Response Format
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": { ... }
}
```

### Network Configuration
- **Timeout**: Connect / Read / Write = **30 detik**
- **Logging**: HTTP Body level (untuk debugging di Logcat)
- **Cleartext Traffic**: Diaktifkan (`android:usesCleartextTraffic="true"`)
- **Permission**: `android.permission.INTERNET`

---

## 🔐 Authentication

Sistem autentikasi menggunakan **JWT (JSON Web Token)** yang dikelola melalui `SessionManager` berbasis **SharedPreferences**.

### Alur Autentikasi

```
┌──────────┐     POST /api/login      ┌──────────┐
│  Login   │ ──────────────────────▶  │  Backend │
│  Screen  │                          │   API    │
│          │ ◀────────────────────── │          │
└────┬─────┘   { token, user }        └──────────┘
     │
     ▼
┌──────────────┐
│SessionManager│  Simpan: JWT Token, nama,
│ (SharedPrefs)│  email, role, status login
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ MainActivity │  Auto-login di sesi berikutnya
└──────────────┘
```

### Fitur Auth
- **Login** dengan validasi email & password
- **Register** dengan konfirmasi password dan role default `user`
- **Auto-Login** — Cek status sesi saat app dibuka
- **Logout** — Hapus seluruh data sesi (clear SharedPreferences)
- **JWT Bearer Token** — Dikirim di Authorization header untuk endpoint yang dilindungi
- **Role-Based** — Mendukung role `admin` dan `user` melalui enum `UserRole`

### Data Sesi yang Disimpan
| Key | Deskripsi |
|:----|:----------|
| `jwt_token` | Token autentikasi JWT |
| `is_logged_in` | Status login (Boolean) |
| `user_name` | Nama pengguna |
| `user_email` | Email pengguna |
| `user_role` | Role pengguna (admin/user) |

---

## 📦 Dependencies

### Core Android
| Library | Versi | Fungsi |
|:--------|:-----:|:-------|
| AndroidX Core KTX | 1.18.0 | Kotlin extensions untuk Android core |
| AppCompat | 1.7.1 | Backward compatibility |
| Material | 1.13.0 | Material Design 3 components |
| ConstraintLayout | 2.2.1 | Flexible layout manager |
| Activity | 1.13.0 | Activity extensions |

### Networking
| Library | Versi | Fungsi |
|:--------|:-----:|:-------|
| Retrofit | 2.11.0 | Type-safe HTTP client |
| Gson Converter | 2.11.0 | JSON ↔ Kotlin serialization |
| OkHttp Logging | 4.12.0 | HTTP request/response logging |

### Architecture Components
| Library | Versi | Fungsi |
|:--------|:-----:|:-------|
| Lifecycle ViewModel KTX | 2.8.7 | ViewModel + Coroutines scope |
| Lifecycle LiveData KTX | 2.8.7 | Observable data holder |

### Image Loading
| Library | Versi | Fungsi |
|:--------|:-----:|:-------|
| Glide | 4.16.0 | Async image loading & caching |

### Testing
| Library | Versi | Fungsi |
|:--------|:-----:|:-------|
| JUnit | 4.13.2 | Unit testing framework |
| AndroidX JUnit | 1.3.0 | Android JUnit extensions |
| Espresso Core | 3.7.0 | UI testing framework |

---

## 📸 Screenshots

> 🚧 **Screenshots akan ditambahkan segera.**

| Login | Register | Home | Transaksi |
|:-----:|:--------:|:----:|:---------:|
| ![Login](screenshots/login.png) | ![Register](screenshots/register.png) | ![Home](screenshots/home.png) | ![Transaction](screenshots/transaction.png) |

| Tagihan | Pembayaran | Pengumuman | Profil |
|:-------:|:----------:|:----------:|:------:|
| ![Bills](screenshots/bills.png) | ![Payment](screenshots/payment.png) | ![Announcement](screenshots/announcement.png) | ![Profile](screenshots/profile.png) |

---

## 🔮 Future Development

| Fitur | Prioritas | Deskripsi |
|:------|:---------:|:----------|
| 📊 Dashboard Statistik | 🔴 High | Grafik pemasukan & pengeluaran di HomeFragment |
| 🔔 Push Notification | 🔴 High | Notifikasi tagihan jatuh tempo & pengumuman baru |
| 🔍 Search & Filter | 🟡 Medium | Pencarian dan filter transaksi/tagihan |
| 📤 Export Laporan | 🟡 Medium | Export riwayat transaksi ke PDF/Excel |
| 🖼️ Upload Avatar | 🟡 Medium | Foto profil pengguna |
| 🗄️ Offline Cache | 🟡 Medium | Room Database untuk mode offline |
| 🔒 Biometric Auth | 🟢 Low | Login dengan fingerprint/face unlock |
| 🌍 Multi-Language | 🟢 Low | Dukungan bahasa Inggris |
| 🧪 Unit & UI Tests | 🟢 Low | Peningkatan code coverage testing |
| 📱 Tablet Layout | 🟢 Low | Responsive layout untuk tablet |

---

## 👨‍💻 Developer

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/FlashDIvine">
        <img src="https://github.com/FlashDIvine.png" width="100px;" alt="Developer" style="border-radius: 50%;" /><br />
        <sub><b>FlashDIvine</b></sub>
      </a>
      <br />
      <a href="https://github.com/FlashDIvine/GrowSave-Android">📱 Project</a>
    </td>
  </tr>
</table>

---

## 📄 License

This project is created for educational purposes.

---

<p align="center">
  <b>⭐ Jangan lupa berikan star jika project ini bermanfaat! ⭐</b>
</p>

<p align="center">
  Made with ❤️ using Kotlin & Android
</p>
