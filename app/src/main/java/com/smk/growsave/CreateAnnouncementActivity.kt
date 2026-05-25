package com.smk.growsave

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.smk.growsave.databinding.ActivityCreateAnnouncementBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.AnnouncementViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateAnnouncementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAnnouncementBinding
    private lateinit var announcementViewModel: AnnouncementViewModel
    private lateinit var sessionManager: SessionManager
    private var selectedImageUri: Uri? = null

    // Register ActivityResultLauncher for gallery image picking
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivImagePreview.setImageURI(uri)
            binding.ivImagePreview.visibility = View.VISIBLE
            binding.layoutUploadPrompt.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAnnouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        announcementViewModel = ViewModelProvider(this)[AnnouncementViewModel::class.java]

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSelectImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etAnnouncementTitle.text.toString().trim()
            val content = binding.etAnnouncementContent.text.toString().trim()

            if (title.isEmpty()) {
                binding.etAnnouncementTitle.error = "Judul pengumuman tidak boleh kosong"
                return@setOnClickListener
            }
            if (content.isEmpty()) {
                binding.etAnnouncementContent.error = "Konten pengumuman tidak boleh kosong"
                return@setOnClickListener
            }

            val category = getSelectedCategory()

            // Prepare multipart parts
            val titleBody = title.toRequestBody(MultipartBody.FORM)
            val contentBody = content.toRequestBody(MultipartBody.FORM)
            val categoryBody = category.toRequestBody(MultipartBody.FORM)

            var imagePart: MultipartBody.Part? = null
            if (selectedImageUri != null) {
                try {
                    val contentResolver = contentResolver
                    val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                    val bytes = inputStream?.readBytes() ?: byteArrayOf()
                    inputStream?.close()

                    val mimeType = contentResolver.getType(selectedImageUri!!) ?: "image/jpeg"
                    val mediaType = mimeType.toMediaTypeOrNull()
                    val requestFile = bytes.toRequestBody(mediaType)
                    imagePart = MultipartBody.Part.createFormData("image", "announcement_image.jpg", requestFile)
                } catch (e: Exception) {
                    Toast.makeText(this, "Gagal memproses gambar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val token = sessionManager.getToken()
            if (token != null) {
                announcementViewModel.createAnnouncement(token, titleBody, contentBody, categoryBody, imagePart)
            } else {
                Toast.makeText(this, "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSelectedCategory(): String {
        val checkedChipId = binding.cgCategory.checkedChipId
        if (checkedChipId != View.NO_ID) {
            val chip = findViewById<Chip>(checkedChipId)
            return chip.text.toString()
        }
        return "Lainnya"
    }

    private fun setupObservers() {
        announcementViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        announcementViewModel.createAnnouncementSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Pengumuman berhasil dibuat!", Toast.LENGTH_SHORT).show()
                announcementViewModel.resetCreateAnnouncementSuccess()
                finish()
            }
        }

        announcementViewModel.errorMessage.observe(this) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }
}
