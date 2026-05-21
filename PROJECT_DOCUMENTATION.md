# 📋 GrowSave — Dokumentasi Progress Project

> **Nama Project**: GrowSave  
> **Platform**: Android (Kotlin)  
> **Arsitektur**: MVVM (Model-View-ViewModel)  
> **Package**: `com.smk.growsave`  
> **Min SDK**: 24 | **Target SDK**: 36  
> **Terakhir Diperbarui**: 21 Mei 2026

---

## 1. 🏗️ Arsitektur Project

Project ini menggunakan pola arsitektur **MVVM (Model-View-ViewModel)** dengan komponen-komponen berikut:

```
┌──────────────────────────────────────────────────────┐
│                        VIEW                          │
│   Activity / Fragment (UI + ViewBinding)              │
│   Mengamati (observe) data dari ViewModel             │
└────────────────────────┬─────────────────────────────┘
                         │ LiveData
┌────────────────────────▼─────────────────────────────┐
│                     VIEWMODEL                        │
│   Mengelola UI State (loading, success, error)       │
│   Menjalankan Coroutines (viewModelScope)             │
└────────────────────────┬─────────────────────────────┘
                         │ suspend fun
┌────────────────────────▼─────────────────────────────┐
│                    REPOSITORY                        │
│   Mediator antara ViewModel dan sumber data (API)     │
│   Menambahkan format "Bearer token"                  │
└────────────────────────┬─────────────────────────────┘
                         │ Retrofit Call
┌────────────────────────▼─────────────────────────────┐
│                   NETWORK LAYER                      │
│   ApiService (Retrofit Interface)                     │
│   RetrofitClient (Singleton + OkHttp + Gson)          │
└──────────────────────────────────────────────────────┘
```

### Teknologi yang Digunakan:

| Komponen | Teknologi |
|---|---|
| **Bahasa** | Kotlin |
| **Arsitektur** | MVVM |
| **Network** | Retrofit 2 + OkHttp 3 |
| **JSON Parser** | Gson Converter |
| **Reactive Data** | LiveData |
| **Async** | Kotlin Coroutines (`viewModelScope`) |
| **UI Binding** | ViewBinding |
| **Image Loader** | Glide 4 |
| **Session** | SharedPreferences (`SessionManager`) |
| **Navigation** | BottomNavigationView + Fragment |

---

## 2. 📁 Struktur Folder Project

```
app/src/main/
├── AndroidManifest.xml
├── java/com/smk/growsave/
│   │
│   ├── LoginActivity.kt              ← Halaman Login (Launcher)
│   ├── MainActivity.kt               ← Container utama + Bottom Navigation
│   │
│   ├── adapter/                       ← RecyclerView Adapters
│   │   ├── AnnouncementAdapter.kt
│   │   ├── BillAdapter.kt
│   │   └── TransactionAdapter.kt
│   │
│   ├── fragment/                      ← UI Fragments
│   │   ├── HomeFragment.kt
│   │   ├── TransactionFragment.kt
│   │   ├── AnnouncementFragment.kt
│   │   ├── BillsFragment.kt
│   │   └── ProfileFragment.kt
│   │
│   ├── model/                         ← Data Models
│   │   ├── BaseResponse.kt            ← Generic API response wrapper
│   │   ├── User.kt                    ← Model user + enum UserRole
│   │   ├── Transaction.kt
│   │   ├── Announcement.kt
│   │   ├── Bill.kt
│   │   └── auth/
│   │       ├── LoginRequest.kt
│   │       └── LoginResponse.kt
│   │
│   ├── network/                       ← Retrofit Configuration
│   │   ├── ApiService.kt              ← Interface endpoint API
│   │   └── RetrofitClient.kt          ← Singleton Retrofit + OkHttp
│   │
│   ├── repository/                    ← Data Repositories
│   │   ├── AuthRepository.kt
│   │   ├── TransactionRepository.kt
│   │   ├── AnnouncementRepository.kt
│   │   └── BillRepository.kt
│   │
│   ├── utils/                         ← Utility Classes
│   │   └── SessionManager.kt          ← JWT Token + Session management
│   │
│   └── viewmodel/                     ← ViewModels (MVVM)
│       ├── AuthViewModel.kt
│       ├── TransactionViewModel.kt
│       ├── AnnouncementViewModel.kt
│       └── BillViewModel.kt
│
└── res/
    ├── layout/
    │   ├── activity_login.xml         ← Form login
    │   ├── activity_main.xml          ← FrameLayout + BottomNavigationView
    │   ├── fragment_home.xml
    │   ├── fragment_transaction.xml   ← RecyclerView + ProgressBar
    │   ├── fragment_announcement.xml  ← RecyclerView + ProgressBar
    │   ├── fragment_bills.xml         ← RecyclerView + ProgressBar
    │   ├── fragment_profile.xml       ← Welcome text + Logout button
    │   ├── item_transaction.xml       ← Item layout RecyclerView
    │   ├── item_announcement.xml      ← Item layout RecyclerView (+ ImageView)
    │   └── item_bill.xml             ← Item layout RecyclerView
    │
    ├── drawable/                      ← Vector icons untuk navigasi
    │   ├── ic_home.xml
    │   ├── ic_transaction.xml
    │   ├── ic_announcement.xml
    │   └── ic_profile.xml
    │
    └── menu/
        └── bottom_menu.xml           ← Menu item Bottom Navigation
```

---

## 3. ✅ Fitur yang Sudah Selesai

### 🔐 Authentication
| Fitur | Status | Keterangan |
|---|---|---|
| Login (email + password) | ✅ Selesai | `LoginActivity` + `AuthViewModel` |
| Session Login (token simpan) | ✅ Selesai | `SessionManager` (SharedPreferences) |
| Auto Login | ✅ Selesai | Cek `isLoggedIn()` di `LoginActivity.onCreate()` |
| Logout | ✅ Selesai | `clearSession()` di `ProfileFragment` |
| Redirect jika belum login | ✅ Selesai | Cek di `MainActivity.onCreate()` |

### 🧭 Navigasi
| Fitur | Status | Keterangan |
|---|---|---|
| Bottom Navigation | ✅ Selesai | 4 tab: Home, Transaction, Announcement, Profile |
| Fragment switching | ✅ Selesai | `supportFragmentManager.replace()` |

### 📊 Data & API Integration
| Fitur | Status | Keterangan |
|---|---|---|
| Transactions API | ✅ Selesai | RecyclerView + warna income/expense |
| Announcements API | ✅ Selesai | RecyclerView + Glide image loading |
| Bills API | ✅ Selesai | RecyclerView + warna status paid/unpaid |

### 🎨 UI Components
| Fitur | Status | Keterangan |
|---|---|---|
| RecyclerView (3 fitur) | ✅ Selesai | Transaction, Announcement, Bill |
| Format Rupiah otomatis | ✅ Selesai | `NumberFormat` locale `id-ID` |
| Glide Image Loading | ✅ Selesai | Gambar pengumuman dari URL |
| Loading State (ProgressBar) | ✅ Selesai | Di setiap fragment data |
| Error Handling (Toast) | ✅ Selesai | Di setiap fragment data |

---

## 4. 🌐 Endpoint API yang Sudah Terintegrasi

| Method | Endpoint | Auth | Deskripsi | File Terkait |
|---|---|---|---|---|
| `POST` | `/api/login` | ❌ | Login pengguna | `AuthRepository.kt` |
| `GET` | `/api/transactions` | ✅ Bearer | Daftar transaksi | `TransactionRepository.kt` |
| `GET` | `/api/announcements` | ✅ Bearer | Daftar pengumuman | `AnnouncementRepository.kt` |
| `GET` | `/api/bills` | ✅ Bearer | Daftar tagihan | `BillRepository.kt` |

### Base URL
```
http://10.0.2.2:8000/
```
> `10.0.2.2` adalah alias localhost untuk Android Emulator.  
> Untuk device fisik, ganti dengan IP address komputer backend.

### Format Response Standar
```json
{
  "success": true,
  "message": "Success",
  "data": { }
}
```
Dibungkus dengan generic class `BaseResponse<T>`.

---

## 5. 📦 Model yang Sudah Dibuat

| Model | Package | Field Utama |
|---|---|---|
| `BaseResponse<T>` | `model` | `success`, `message`, `data` |
| `User` | `model` | `id`, `name`, `email`, `role` + enum `UserRole` |
| `LoginRequest` | `model.auth` | `email`, `password` |
| `LoginResponse` | `model.auth` | `token`, `user` |
| `Transaction` | `model` | `id`, `title`, `type`, `amount`, `createdAt` |
| `Announcement` | `model` | `id`, `title`, `content`, `imageUrl`, `createdAt` |
| `Bill` | `model` | `id`, `title`, `amount`, `dueDate`, `status` |

---

## 6. 🗂️ Repository & ViewModel

### Repository
| Repository | Fungsi | Token |
|---|---|---|
| `AuthRepository` | `login(request)` | ❌ |
| `TransactionRepository` | `getTransactions(token)` | ✅ Bearer |
| `AnnouncementRepository` | `getAnnouncements(token)` | ✅ Bearer |
| `BillRepository` | `getBills(token)` | ✅ Bearer |

### ViewModel
| ViewModel | LiveData | Digunakan di |
|---|---|---|
| `AuthViewModel` | `loginResult`, `isLoading`, `errorMessage` | `LoginActivity` |
| `TransactionViewModel` | `transactions`, `isLoading`, `errorMessage` | `TransactionFragment` |
| `AnnouncementViewModel` | `announcements`, `isLoading`, `errorMessage` | `AnnouncementFragment` |
| `BillViewModel` | `bills`, `isLoading`, `errorMessage` | `BillsFragment` |

> Semua ViewModel diinisialisasi menggunakan `ViewModelProvider(this)[ClassName::class.java]` (tanpa library `fragment-ktx`).

---

## 7. ⚙️ Catatan Konfigurasi Penting

### AndroidManifest.xml
```xml
<!-- Izin akses internet -->
<uses-permission android:name="android.permission.INTERNET"/>

<!-- Mengizinkan HTTP (non-HTTPS) untuk development lokal -->
<application android:usesCleartextTraffic="true" ... >
```

### Launcher Activity
```
LoginActivity → (auto login check) → MainActivity
```
`LoginActivity` adalah Activity pertama yang dibuka (`LAUNCHER`). Jika user sudah login (`isLoggedIn() == true`), langsung redirect ke `MainActivity`.

### Authorization Header
Semua endpoint selain `/api/login` memerlukan header:
```
Authorization: Bearer {jwt_token}
```
Token diambil dari `SessionManager.getToken()` dan ditambahkan prefix "Bearer " di setiap Repository.

### SessionManager (SharedPreferences)
| Key | Tipe | Keterangan |
|---|---|---|
| `jwt_token` | String | Token JWT dari server |
| `is_logged_in` | Boolean | Status login |
| `user_name` | String | Nama pengguna |
| `user_email` | String | Email pengguna |
| `user_role` | String | Role (admin/user) |

---

## 8. 📚 Dependency yang Sudah Digunakan

```kotlin
// app/build.gradle.kts

// Networking
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Lifecycle (ViewModel + LiveData)
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

// Image Loading
implementation("com.github.bumptech.glide:glide:4.16.0")

// Android Core (via Version Catalog)
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.appcompat)
implementation(libs.material)
implementation(libs.androidx.activity)
implementation(libs.androidx.constraintlayout)
```

### Build Features
```kotlin
buildFeatures {
    viewBinding = true  // Mengaktifkan ViewBinding
}
```

---

## 9. 📍 Progress Saat Ini

```
███████████████████████░░░░░░░  75% — Fase 1 (Core Features) Selesai
```

### Fase yang Sudah Selesai:
- ✅ **Fase 0**: Setup Networking (Retrofit, OkHttp, Gson)
- ✅ **Fase 1A**: Authentication (Login, Session, Auto-login, Logout)
- ✅ **Fase 1B**: Main Navigation (Bottom Navigation + 4 Fragment)
- ✅ **Fase 1C**: API Integration (Transactions, Announcements, Bills)

### Fase yang Sedang Dikerjakan:
- 🔄 **Fase 2**: Payment Integration (Midtrans)
  - `POST /api/payments` → mendapatkan `snap_token`
  - Midtrans SDK untuk membuka halaman pembayaran

---

## 10. 📝 TODO Selanjutnya

### 🔴 Prioritas Tinggi
- [ ] **Payment API** — Endpoint `POST /api/payments` + model `PaymentRequest` & `PaymentResponse`
- [ ] **Midtrans SDK** — Install dependency, setup SDK, buka payment page dari `snap_token`
- [ ] **Register** — Endpoint `POST /api/register` + `RegisterActivity`

### 🟡 Prioritas Sedang
- [ ] **Profile Update** — Endpoint `PUT /api/profile` + edit profil di `ProfileFragment`
- [ ] **Bill Refresh** — Refresh daftar tagihan setelah pembayaran berhasil
- [ ] **Pull-to-Refresh** — SwipeRefreshLayout di setiap Fragment data
- [ ] **Empty State** — Tampilkan pesan/gambar jika data kosong

### 🟢 Prioritas Rendah (Polish)
- [ ] **UI Redesign** — Redesign berdasarkan Figma (warna, font, layout premium)
- [ ] **Splash Screen** — Halaman pembuka dengan logo GrowSave
- [ ] **Token Expiry Handling** — Auto logout jika token kadaluarsa (401)
- [ ] **Production Release** — Ganti Base URL, disable Cleartext, ProGuard, signing APK

---

## 🔗 Quick Reference — Alur Data MVVM

```
User klik tombol login
  → LoginActivity memanggil viewModel.login(email, password)
    → AuthViewModel menjalankan coroutine di viewModelScope
      → AuthRepository memanggil apiService.login(request)
        → Retrofit mengirim POST ke /api/login
        ← Server mengembalikan JSON response
      ← Repository return BaseResponse<LoginResponse>
    ← ViewModel update LiveData (_loginResult.value = response)
  ← LoginActivity observe loginResult → update UI
```

---

> **Catatan**: Dokumentasi ini dibuat agar project bisa dilanjutkan kapan saja tanpa kebingungan.  
> Setiap fitur baru yang ditambahkan, mohon perbarui dokumen ini agar tetap akurat.
