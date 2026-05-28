# 📋 GrowSave — Dokumentasi Progress Project

> **Nama Project**: GrowSave  
> **Platform**: Android (Kotlin)  
> **Arsitektur**: MVVM (Model-View-ViewModel)  
> **Package**: `com.smk.growsave`  
> **Min SDK**: 24 | **Target SDK**: 36  
> **Terakhir Diperbarui**: 28 Mei 2026

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
│   │   ├── RoomRequestAdapter.kt      ← Adapter permohonan gabung room
│   │   └── TransactionAdapter.kt
│   │
│   ├── fragment/                      ← UI Fragments
│   │   ├── HomeFragment.kt
│   │   ├── TransactionFragment.kt
│   │   ├── AnnouncementFragment.kt
│   │   ├── FinanceFragment.kt         ← Menggantikan BillsFragment (Dashboard Iuran & Midtrans)
│   │   ├── ResidentsFragment.kt       ← Daftar warga & permohonan room
│   │   └── ProfileFragment.kt
│   │
│   ├── model/                         ← Data Models
│   │   ├── BaseResponse.kt            ← Generic API response wrapper
│   │   ├── User.kt                    ← Model user + enum UserRole
│   │   ├── Transaction.kt
│   │   ├── Announcement.kt
│   │   ├── Bill.kt
│   │   ├── BillStats.kt               ← Model statistik iuran (lokal)
│   │   ├── RoomMember.kt              ← Model anggota/penghuni room
│   │   ├── RoomRequest.kt             ← Model pengajuan gabung room
│   │   ├── PaymentRequest.kt          ← Request Snap Token Midtrans
│   │   ├── PaymentResponse.kt         ← Response Snap Token Midtrans
│   │   └── auth/
│   │       ├── LoginRequest.kt
│   │       └── LoginResponse.kt
│   │
│   ├── network/                       ← Retrofit Configuration
│   │   ├── ApiService.kt              ← Interface endpoint API
│   │   └── RetrofitClient.kt          ← Singleton Retrofit + OkHttp
│   ├── repository/                    ← Data Repositories
│   │   ├── AuthRepository.kt
│   │   ├── TransactionRepository.kt
│   │   ├── AnnouncementRepository.kt
│   │   ├── BillRepository.kt
│   │   └── PaymentRepository.kt
│   │
│   ├── utils/                         ← Utility Classes
│   └── viewmodel/                     ← ViewModels (MVVM)
│
└── res/
    ├── layout/
    ├── drawable/
    └── menu/
```

---

## 3. ✅ Fitur yang Sudah Selesai

### 🔐 Authentication
| Fitur | Status | Keterangan |
|---|---|---|
| Login (email + password) | ✅ Selesai | `LoginActivity` + `AuthViewModel` |
| Register (Admin & User) | ✅ Selesai | Pendaftaran akun baru. Admin divalidasi backend via `admin_code`, User menggunakan `room_code`. |
| Session Login (token simpan) | ✅ Selesai | `SessionManager` (SharedPreferences) |
| Auto Login | ✅ Selesai | Cek `isLoggedIn()` di `LoginActivity.onCreate()` |
| Logout | ✅ Selesai | `clearSession()` di `ProfileFragment` |
| Redirect jika belum login | ✅ Selesai | Cek di `MainActivity.onCreate()` |

### 🧭 Navigasi & Safety Guards
| Fitur | Status | Keterangan |
|---|---|---|
| Bottom Navigation | ✅ Selesai | 4 tab: Home, Residents, Finance, Profile |
| Fragment switching | ✅ Selesai | `supportFragmentManager.replace()` |
| Navigation Lock Guard | ✅ Selesai | Mencegah race condition/spam klik tombol navigasi |
| Duplicate Fragment Guard | ✅ Selesai | Menghindari reload fragment jika tujuan sama dengan aktif |

### 📊 Data & API Integration
| Fitur | Status | Keterangan |
|---|---|---|
| Transactions API | ✅ Selesai | RecyclerView + warna income/expense |
| Announcements API | ✅ Selesai | RecyclerView + Glide image loading |
| Bills API | ✅ Selesai | RecyclerView + warna status paid/unpaid |
| Midtrans Snap API | ✅ Selesai | Pembayaran tagihan dengan Snap Token Midtrans |
| Room Approval API | ✅ Selesai | Mengambil permohonan masuk, approve/reject gabung room |

### 🎨 UI Components & Dashboard Stats
| Fitur | Status | Keterangan |
|---|---|---|
| RecyclerView (5 fitur) | ✅ Selesai | Transaction, Announcement, Bill, Resident, RoomRequest |
| Segmented Tab Control | ✅ Selesai | Beralih antara daftar warga & permohonan di `ResidentsFragment` |
| Local Calculated Stats | ✅ Selesai | `BillStats` dihitung locally dengan `LiveData.map` tanpa panggil API tambahan |
| Dashboard Stat Cards | ✅ Selesai | Desain card premium menampilkan total iuran unpaid, active bills, dll. |
| Format Rupiah otomatis | ✅ Selesai | `NumberFormat` locale `id-ID` |
| Glide Image Loading | ✅ Selesai | Gambar pengumuman dari URL |
| Loading, Empty, Error State | ✅ Selesai | Layout loader, visualisasi kosong, dan error handler dengan retry |

---

## 4. 🌐 Endpoint API yang Sudah Terintegrasi

| Method | Endpoint | Auth | Deskripsi | File Terkait |
|---|---|---|---|---|
| `POST` | `/api/login` | ❌ | Login pengguna | `AuthRepository.kt` |
| `POST` | `/api/register` | ❌ | Register user / admin baru | `AuthRepository.kt` |
| `GET` | `/api/transactions` | ✅ Bearer | Daftar transaksi | `TransactionRepository.kt` |
| `GET` | `/api/announcements` | ✅ Bearer | Daftar pengumuman | `AnnouncementRepository.kt` |
| `GET` | `/api/bills` | ✅ Bearer | Daftar tagihan | `BillRepository.kt` |
| `POST` | `/api/bills/{id}/complete` | ✅ Bearer | Menyelesaikan iuran manual (admin) | `BillRepository.kt` |
| `DELETE` | `/api/bills/{id}` | ✅ Bearer | Menghapus tagihan (admin) | `BillRepository.kt` |
| `POST` | `/api/payments` | ✅ Bearer | Membuat snap token pembayaran | `PaymentRepository.kt` |
| `GET` | `/api/room/requests` | ✅ Bearer | Daftar request gabung room | `AuthRepository.kt` |
| `GET` | `/api/room/residents` | ✅ Bearer | Daftar warga aktif di room | `AuthRepository.kt` |
| `POST` | `/api/room/approve/{id}` | ✅ Bearer | Menyetujui request gabung (admin) | `AuthRepository.kt` |
| `POST` | `/api/room/reject/{id}` | ✅ Bearer | Menolak request gabung (admin) | `AuthRepository.kt` |

### Base URL
```
http://10.0.2.2:8000/
```

---

## 5. 📦 Model yang Sudah Dibuat

| Model | Package | Field Utama |
|---|---|---|
| `BaseResponse<T>` | `model` | `success`, `message`, `data` |
| `User` | `model` | `id`, `name`, `email`, `role` + enum `UserRole` |
| `LoginRequest` | `model.auth` | `email`, `password` |
| `LoginResponse` | `model.auth` | `token`, `user` |
| `RegisterRequest` | `model.auth` | `name`, `email`, `password`, `adminCode`, `roomCode` |
| `Transaction` | `model` | `id`, `title`, `type`, `amount`, `createdAt` |
| `Announcement` | `model` | `id`, `title`, `content`, `imageUrl`, `createdAt` |
| `Bill` | `model` | `id`, `title`, `amount`, `dueDate`, `status` |
| `BillStats` | `model` | `totalBills`, `activeBills`, `paidBills`, `totalUnpaidAmount` |
| `RoomRequest` | `model` | `id`, `user`, `roomCode`, `status` |
| `RoomMember` | `model` | `id`, `roomId`, `userId`, `status`, `joinedAt`, `user` |
| `PaymentRequest` | `model` | `billId` |
| `PaymentResponse` | `model` | `snapToken` |

---

## 6. 🗂️ Repository & ViewModel

### Repository
| Repository | Fungsi | Token |
|---|---|---|
| `AuthRepository` | `login(req)`, `register(req)`, `fetchRoomRequests()`, `fetchRoomResidents()`, `approveRoom()`, `rejectRoom()` | ❌ / ✅ |
| `TransactionRepository` | `getTransactions(token)`, `createTransaction()` | ✅ Bearer |
| `AnnouncementRepository` | `getAnnouncements(token)`, `createAnnouncement()`, `deleteAnnouncement()` | ✅ Bearer |
| `BillRepository` | `getBills(token)`, `createBill()`, `completeBill()`, `deleteBill()` | ✅ Bearer |
| `PaymentRepository` | `createPayment(token, billId)` | ✅ Bearer |

### ViewModel
| ViewModel | LiveData | Digunakan di |
|---|---|---|
| `AuthViewModel` | `loginResult`, `roomResidents`, `roomRequests`, `roomActionSuccess`, `isLoading`, `errorMessage` | `LoginActivity`, `RegisterActivity`, `RegisterAdminActivity`, `ResidentsFragment` |
| `TransactionViewModel` | `transactions`, `isLoading`, `errorMessage` | `TransactionFragment` |
| `AnnouncementViewModel` | `announcements`, `isLoading`, `errorMessage` | `AnnouncementFragment` |
| `BillViewModel` | `bills`, `billStats`, `isLoading`, `errorMessage`, `completeBillSuccess`, `deleteBillSuccess` | `FinanceFragment` |
| `PaymentViewModel` | `snapToken`, `isLoading`, `errorMessage` | `FinanceFragment`, `PaymentActivity` |

---

## 7. ⚙️ Catatan Konfigurasi Penting

### AndroidManifest.xml
```xml
<!-- Izin akses internet -->
<uses-permission android:name="android.permission.INTERNET"/>

<!-- Mengizinkan HTTP (non-HTTPS) untuk development lokal -->
<application android:usesCleartextTraffic="true" ... >
```

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

// Android Core
implementation(libs.androidx.core.ktx)
implementation(libs.appcompat)
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
████████████████████████████░░  95% — Fase 2 (Payment & Room Management) Selesai
```

### Fase yang Sudah Selesai:
- ✅ **Fase 0**: Setup Networking (Retrofit, OkHttp, Gson)
- ✅ **Fase 1**: Authentication & Navigation (Login, Session, Navigation, Register Admin & User)
- ✅ **Fase 2**: Payment & Room Management (Midtrans Snap WebView Integration, Room Approval/Rejection System, Local Calculated Dashboard Stats, Navigation Safety Guards)

### Fase yang Sedang Dikerjakan:
- 🔄 **Fase 3**: Bug Fixing & Production Release
  - Pengujian komprehensif aliran pembayaran online dan penanganan status respons

---

## 10. 📝 TODO Selanjutnya

### 🔴 Prioritas Tinggi
- [ ] **Testing Integrasi Midtrans** — Pengujian menyeluruh alur pembayaran di Sandbox dari Snap Token hingga status pembaruan transaksi.

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
