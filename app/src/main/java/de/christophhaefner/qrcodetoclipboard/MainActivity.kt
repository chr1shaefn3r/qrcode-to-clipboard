package de.christophhaefner.qrcodetoclipboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

private const val REQUEST_CAMERA = 1

/** How long a code has to be out of view before scanning it again counts as a new scan. */
private const val RESCAN_AFTER_MS = 2000L

class MainActivity : Activity() {

    private lateinit var scanner: BarcodeView
    private lateinit var status: TextView

    private var lastValue: String? = null
    private var lastSeenAt = 0L
    private var permissionRequested = false

    private val callback = BarcodeCallback { result ->
        val value = result.text ?: return@BarcodeCallback
        val now = SystemClock.elapsedRealtime()
        val isSameCodeStillInView = value == lastValue && now - lastSeenAt < RESCAN_AFTER_MS
        lastValue = value
        lastSeenAt = now
        if (!isSameCodeStillInView) onScanned(value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        status = findViewById(R.id.status)
        scanner = findViewById(R.id.scanner)
        scanner.decoderFactory = DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
        scanner.decodeContinuous(callback)

        findViewById<View>(R.id.history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        status.setOnClickListener {
            if (!hasCameraPermission()) requestCameraPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasCameraPermission()) {
            scanner.resume()
        } else if (!permissionRequested) {
            requestCameraPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        scanner.pause()
    }

    private fun onScanned(value: String) {
        copyToClipboard(value)
        History.add(this, value)
        status.text = getString(R.string.copied, value)
        scanner.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    private fun hasCameraPermission() =
        checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() {
        if (permissionRequested && !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // Denied permanently: the system dialog will not show again, so send the user to the app settings.
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
            )
            return
        }
        permissionRequested = true
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CAMERA) return
        if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            status.setText(R.string.scan_hint)
            // onResume follows and starts the camera.
        } else {
            status.setText(R.string.camera_permission_needed)
        }
    }
}
