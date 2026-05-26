package com.smk.growsave

import org.junit.Test
import org.junit.Assert.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.TransactionResponse
import com.smk.growsave.model.RoomDetailResponse

/**
 * Unit test untuk memverifikasi proses deserialisasi JSON response
 * dari endpoint GET /api/transactions dan GET /api/room.
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testRoomResponseParsing() {
        val gson = Gson()
        val type = object : TypeToken<BaseResponse<RoomDetailResponse>>() {}.type

        // 1. Uji parsing response room untuk role ADMIN (flat schema)
        val adminJson = """
        {
          "success": true,
          "message": "Data room admin",
          "data": {
            "id": 3,
            "room_name": "abib's Room",
            "room_code": "ROOM-1EHWGI",
            "description": null,
            "status": "active",
            "total_members": 1
          }
        }
        """.trimIndent()

        val adminResponse: BaseResponse<RoomDetailResponse> = gson.fromJson(adminJson, type)
        assertTrue(adminResponse.success)
        val adminData = adminResponse.data
        assertNotNull(adminData)
        assertEquals("abib's Room", adminData!!.roomName)
        assertEquals("ROOM-1EHWGI", adminData.roomCode)
        assertEquals("active", adminData.status)
        assertEquals(1, adminData.totalMembers)

        // 2. Uji parsing response room untuk role USER (nested room schema)
        val userJson = """
        {
          "success": true,
          "message": "Data room user",
          "data": {
            "membership_status": "approved",
            "room": {
              "id": 3,
              "room_name": "abib's Room",
              "room_code": "ROOM-1EHWGI",
              "description": null,
              "status": "active"
            }
          }
        }
        """.trimIndent()

        val userResponse: BaseResponse<RoomDetailResponse> = gson.fromJson(userJson, type)
        assertTrue(userResponse.success)
        val userData = userResponse.data
        assertNotNull(userData)
        assertEquals("abib's Room", userData!!.roomName)
        assertEquals("ROOM-1EHWGI", userData.roomCode)
        assertEquals("active", userData.status)
        assertNull(userData.totalMembers) // total_members tidak ada di response user

        println("=== ROOM TEST SUCCESSFUL ===")
        println("Admin Room Name: ${adminData.roomName}, Members: ${adminData.totalMembers}, Status: ${adminData.status}")
        println("User Room Name: ${userData.roomName}, Status: ${userData.status}")
        println("============================")
    }

    @Test
    fun testTransactionResponseParsing() {
        // Response JSON aktual yang diperoleh dari verifikasi endpoint
        val jsonResponse = """
        {
          "success": true,
          "message": "Data transparansi kas",
          "data": {
            "total_saldo": 13000,
            "pemasukan_bulan_ini": 13000,
            "pengeluaran_bulan_ini": 0,
            "riwayat_transaksi": [
              {
                "id": 3,
                "room_id": 3,
                "user_id": 4,
                "type": "income",
                "category": "Pembayaran Tagihan",
                "amount": 5000,
                "transaction_date": "2026-05-26T00:00:00.000000Z",
                "description": "Pembayaran iuran: rtri45. Source: bill payment, Reference ID: 10",
                "created_at": "2026-05-26T03:25:27.000000Z",
                "updated_at": "2026-05-26T03:25:27.000000Z",
                "deleted_at": null,
                "user": {
                  "id": 4,
                  "name": "abib"
                }
              },
              {
                "id": 2,
                "room_id": 3,
                "user_id": 5,
                "type": "income",
                "category": "Pembayaran Tagihan",
                "amount": 4000,
                "transaction_date": "2026-05-25T00:00:00.000000Z",
                "description": "Pembayaran iuran: 123123. Source: bill payment, Reference ID: 9",
                "created_at": "2026-05-25T03:30:28.000000Z",
                "updated_at": "2026-05-25T03:30:28.000000Z",
                "deleted_at": null,
                "user": {
                  "id": 5,
                  "name": "Najib"
                }
              },
              {
                "id": 1,
                "room_id": 3,
                "user_id": 4,
                "type": "income",
                "category": "Pembayaran Tagihan",
                "amount": 4000,
                "transaction_date": "2026-05-25T00:00:00.000000Z",
                "description": "Pembayaran iuran: 123123. Source: bill payment, Reference ID: 8",
                "created_at": "2026-05-25T03:23:30.000000Z",
                "updated_at": "2026-05-25T03:23:30.000000Z",
                "deleted_at": null,
                "user": {
                  "id": 4,
                  "name": "abib"
                }
              }
            ]
          }
        }
        """.trimIndent()

        val gson = Gson()
        val type = object : TypeToken<BaseResponse<TransactionResponse>>() {}.type
        val response: BaseResponse<TransactionResponse> = gson.fromJson(jsonResponse, type)

        // Verifikasi Root Response
        assertTrue(response.success)
        assertEquals("Data transparansi kas", response.message)

        // Verifikasi Nested Data
        val data = response.data
        assertNotNull(data)
        assertEquals(13000.0, data!!.totalSaldo, 0.001)
        assertEquals(13000.0, data.pemasukanBulanIni, 0.001)
        assertEquals(0.0, data.pengeluaranBulanIni, 0.001)

        // Verifikasi List Transaksi (Riwayat)
        val transactionList = data.riwayatTransaksi
        assertNotNull(transactionList)
        assertTrue(transactionList.isNotEmpty())
        assertEquals(3, transactionList.size)

        // Verifikasi Item Transaksi Pertama
        val firstTx = transactionList[0]
        assertEquals(3, firstTx.id)
        assertEquals("income", firstTx.type)
        assertEquals("Pembayaran Tagihan", firstTx.title)
        assertEquals(5000L, firstTx.amount)
        assertEquals("2026-05-26T00:00:00.000000Z", firstTx.createdAt)

        // Output pembuktian ke console log
        println("=== TEST SUCCESSFUL ===")
        println("BaseResponse parsed successfully!")
        println("Total Saldo: ${data.totalSaldo}")
        println("Riwayat Transaksi size: ${transactionList.size}")
        transactionList.forEachIndexed { index, tx ->
            println("[$index] ID: ${tx.id}, Title (Category): ${tx.title}, Type: ${tx.type}, Amount: ${tx.amount}")
        }
        println("=======================")
    }
}