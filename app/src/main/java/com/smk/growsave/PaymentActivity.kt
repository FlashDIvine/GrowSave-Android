package com.smk.growsave

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.smk.growsave.databinding.ActivityPaymentBinding

/**
 * PaymentActivity membuka halaman pembayaran Midtrans Snap secara asinkron menggunakan WebView.
 */
class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    companion object {
        const val EXTRA_SNAP_TOKEN = "EXTRA_SNAP_TOKEN"
        const val EXTRA_BILL_TITLE = "EXTRA_BILL_TITLE"
        private const val MIDTRANS_SANDBOX_BASE_URL = "https://app.sandbox.midtrans.com/snap/v2/vtweb/"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val snapToken = intent.getStringExtra(EXTRA_SNAP_TOKEN)
        val billTitle = intent.getStringExtra(EXTRA_BILL_TITLE) ?: "Pembayaran Tagihan"

        setupToolbar(billTitle)

        if (snapToken != null) {
            setupWebView(snapToken)
        } else {
            finish()
        }

        setupBackPressedCallback()
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            this.title = title
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(snapToken: String) {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < 100) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE

                // Memeriksa URL redirect jika pembayaran selesai / dibatalkan
                url?.let {
                    if (it.contains("finish") || it.contains("callback") || it.contains("success")) {
                        // Selesai membayar
                        finish()
                    }
                }
            }
        }

        // Memuat URL Snap pembayaran Midtrans
        val paymentUrl = "$MIDTRANS_SANDBOX_BASE_URL$snapToken"
        binding.webView.loadUrl(paymentUrl)
    }

    private fun setupBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                finish()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
