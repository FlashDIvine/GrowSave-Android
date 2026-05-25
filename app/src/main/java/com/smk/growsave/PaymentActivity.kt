package com.smk.growsave

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.smk.growsave.databinding.ActivityPaymentBinding

/**
 * PaymentActivity membuka halaman pembayaran Midtrans Snap secara asinkron menggunakan WebView.
 */
class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var mWebView: WebView? = null
    private var currentSnapToken: String? = null

    companion object {
        private const val TAG = "GrowSavePayment"
        const val EXTRA_SNAP_TOKEN = "EXTRA_SNAP_TOKEN"
        const val EXTRA_BILL_TITLE = "EXTRA_BILL_TITLE"
        private const val MIDTRANS_SANDBOX_BASE_URL = "https://app.sandbox.midtrans.com/snap/v2/vtweb/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val snapToken = intent.getStringExtra(EXTRA_SNAP_TOKEN)
        val billTitle = intent.getStringExtra(EXTRA_BILL_TITLE) ?: "Pembayaran Tagihan"

        setupToolbar(billTitle)

        currentSnapToken = snapToken
        mWebView = binding.webView

        if (snapToken != null) {
            Log.d(TAG, "onCreate: Launching payment flow with snapToken = $snapToken")
            setupWebView(mWebView!!, snapToken)
        } else {
            Log.w(TAG, "onCreate: snapToken was null, finishing activity")
            finish()
        }

        setupBackPressedCallback()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val snapToken = intent.getStringExtra(EXTRA_SNAP_TOKEN)
        val billTitle = intent.getStringExtra(EXTRA_BILL_TITLE) ?: "Pembayaran Tagihan"
        
        setupToolbar(billTitle)
        
        currentSnapToken = snapToken
        if (snapToken != null) {
            Log.d(TAG, "onNewIntent: Re-opening payment flow with snapToken = $snapToken")
            mWebView?.let {
                val paymentUrl = "$MIDTRANS_SANDBOX_BASE_URL$snapToken"
                Log.d(TAG, "Loading URL: $paymentUrl")
                it.loadUrl(paymentUrl)
            } ?: run {
                mWebView = binding.webView
                setupWebView(mWebView!!, snapToken)
            }
        }
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
    private fun setupWebView(webView: WebView, snapToken: String) {
        Log.d(TAG, "Configuring WebView settings")
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            javaScriptCanOpenWindowsAutomatically = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        // Enable and configure Cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < 100) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "onPageStarted: $url")
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished: $url")
                binding.progressBar.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                return handleUrlOverride(view, url)
            }

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return handleUrlOverride(view, url)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                val description = error?.description?.toString() ?: "Unknown error"
                val errorCode = error?.errorCode ?: 0
                Log.e(TAG, "onReceivedError: url=${request?.url}, code=$errorCode, desc=$description")
            }

            override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                val didCrash = detail?.didCrash() == true
                Log.e(TAG, "WebView Renderer Process Gone! didCrash = $didCrash")
                
                // Show recovery message to user
                Toast.makeText(this@PaymentActivity, "Terjadi gangguan pada renderer halaman. Memulihkan...", Toast.LENGTH_LONG).show()
                
                // Recover from crash by recreating the WebView
                currentSnapToken?.let { token ->
                    recreateWebView(token)
                } ?: run {
                    finish()
                }
                
                return true // Return true to indicate we handled the crash and the app process should not be terminated
            }
        }

        // Load the Midtrans Snap Payment URL
        val paymentUrl = "$MIDTRANS_SANDBOX_BASE_URL$snapToken"
        Log.d(TAG, "Loading initial payment URL: $paymentUrl")
        webView.loadUrl(paymentUrl)
    }

    private fun handleUrlOverride(view: WebView?, url: String?): Boolean {
        if (url == null) return false
        Log.d(TAG, "handleUrlOverride: $url")

        // 1. Handle non-HTTP/HTTPS URLs (custom deep links: intent://, gojek://, shopeepay://, etc.)
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Log.d(TAG, "Intercepted custom scheme / deep-link URL: $url")
            try {
                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                if (intent != null) {
                    val packageManager = packageManager
                    val info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                    if (info != null) {
                        Log.d(TAG, "Starting external payment app Activity: ${intent.`package` ?: info.activityInfo.packageName}")
                        startActivity(intent)
                    } else {
                        // Attempt fallback
                        val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                        if (fallbackUrl != null) {
                            Log.d(TAG, "App not installed. Loading fallback URL: $fallbackUrl")
                            view?.loadUrl(fallbackUrl)
                        } else {
                            val packName = intent.`package`
                            if (packName != null) {
                                Log.d(TAG, "App not installed. Directing to Play Store for package: $packName")
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packName")))
                            } else {
                                Toast.makeText(this, "Aplikasi pembayaran tidak ditemukan", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    return true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling deep link intent URI: $url", e)
            }

            // Fallback for general ACTION_VIEW intent (e.g. gojek://)
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            } catch (e: Exception) {
                Log.e(TAG, "Fallback ACTION_VIEW failed for URL: $url", e)
                Toast.makeText(this, "Gagal membuka aplikasi pembayaran", Toast.LENGTH_SHORT).show()
                return true // Return true so WebView doesn't attempt to load it and throw ERR_UNKNOWN_URL_SCHEME
            }
        }

        // 2. Handle Midtrans payment callback redirects and the mock sandbox domain
        if (url.contains("snap-redirection-app.sandbox") || 
            url.contains("finish") || 
            url.contains("callback") || 
            url.contains("success")) {
            
            Log.d(TAG, "Intercepted final callback redirect URL: $url")
            handlePaymentCallback(url)
            return true // Intercept: do not load the redirect page in the WebView
        }

        return false // Let WebView load it
    }

    private fun handlePaymentCallback(url: String) {
        val uri = Uri.parse(url)
        val orderId = uri.getQueryParameter("order_id")
        val statusCode = uri.getQueryParameter("status_code")
        val transactionStatus = uri.getQueryParameter("transaction_status")

        Log.d(TAG, "Payment Callback Details -> OrderID: $orderId, StatusCode: $statusCode, TransactionStatus: $transactionStatus")

        val resultIntent = Intent().apply {
            putExtra("ORDER_ID", orderId)
            putExtra("STATUS_CODE", statusCode)
            putExtra("TRANSACTION_STATUS", transactionStatus)
            putExtra("CALLBACK_URL", url)
        }

        if (transactionStatus != null) {
            when (transactionStatus.lowercase()) {
                "settlement", "capture" -> {
                    Toast.makeText(this, "Pembayaran berhasil!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK, resultIntent)
                }
                "pending" -> {
                    Toast.makeText(this, "Pembayaran pending, silakan selesaikan pembayaran.", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK, resultIntent)
                }
                "deny", "cancel", "expire" -> {
                    Toast.makeText(this, "Pembayaran dibatalkan atau gagal.", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_CANCELED, resultIntent)
                }
                else -> {
                    setResult(RESULT_OK, resultIntent)
                }
            }
        } else {
            // Fallback mapping for simple URL matches
            if (url.contains("success") || url.contains("finish")) {
                Toast.makeText(this, "Pembayaran berhasil!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK, resultIntent)
            } else {
                setResult(RESULT_CANCELED, resultIntent)
            }
        }

        finish()
    }

    private fun recreateWebView(snapToken: String) {
        Log.d(TAG, "Initiating WebView recreation...")
        val parent = binding.webView.parent as? ViewGroup
        if (parent != null) {
            val index = parent.indexOfChild(mWebView)
            
            // Remove old WebView
            mWebView?.let { oldWebView ->
                parent.removeView(oldWebView)
                try {
                    oldWebView.destroy()
                } catch (e: Exception) {
                    Log.e(TAG, "Error destroying old WebView", e)
                }
            }

            // Create new WebView instance programmatically
            val newWebView = WebView(this).apply {
                id = R.id.webView
                layoutParams = binding.webView.layoutParams
            }

            // Add back to parent layout in same position
            parent.addView(newWebView, index)
            mWebView = newWebView

            // Configure settings and clients again
            setupWebView(newWebView, snapToken)
        } else {
            Log.e(TAG, "Parent view group of WebView is null. Cannot recreate WebView.")
            finish()
        }
    }

    private fun setupBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mWebView?.let { webView ->
                    if (webView.canGoBack()) {
                        Log.d(TAG, "handleOnBackPressed: WebView going back")
                        webView.goBack()
                    } else {
                        Log.d(TAG, "handleOnBackPressed: WebView cannot go back, finishing Activity")
                        finish()
                    }
                } ?: finish()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mWebView?.let { webView ->
                if (webView.canGoBack()) {
                    Log.d(TAG, "onOptionsItemSelected: Home clicked, WebView going back")
                    webView.goBack()
                } else {
                    Log.d(TAG, "onOptionsItemSelected: Home clicked, finishing Activity")
                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        mWebView?.onPause()
        super.onPause()
        Log.d(TAG, "onPause: Paused WebView")
    }

    override fun onResume() {
        super.onResume()
        mWebView?.onResume()
        Log.d(TAG, "onResume: Resumed WebView")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mWebView?.saveState(outState)
        Log.d(TAG, "onSaveInstanceState: Saved WebView state")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mWebView?.restoreState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState: Restored WebView state")
    }

    override fun onDestroy() {
        mWebView?.let { webView ->
            (webView.parent as? ViewGroup)?.removeView(webView)
            try {
                webView.destroy()
            } catch (e: Exception) {
                Log.e(TAG, "Error destroying WebView in onDestroy", e)
            }
        }
        mWebView = null
        super.onDestroy()
        Log.d(TAG, "onDestroy: WebView destroyed and removed from layout")
    }
}
