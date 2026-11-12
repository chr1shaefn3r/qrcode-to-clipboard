package de.christophhaefner.qrcodetoclipboard

import android.Manifest
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

/** How long a code has to be out of view before scanning it again counts as a new scan. */
private const val RESCAN_AFTER_MS = 2000L

class MainActivity : AppCompatActivity() {

    private lateinit var scanner: BarcodeView
    private lateinit var status: TextView

    private var lastValue: String? = null
    private var lastSeenAt = 0L
    private var permissionRequested = false

    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // When granted, onResume follows and starts the camera.
            status.setText(if (granted) R.string.scan_hint else R.string.camera_permission_needed)
        }

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_bar)) { bar, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            bar.updatePadding(left = bars.left, right = bars.right, bottom = bars.bottom)
            insets
        }

        findViewById<View>(R.id.history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<View>(R.id.status_card).setOnClickListener {
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
        scanner.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
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
        cameraPermission.launch(Manifest.permission.CAMERA)
    }
}
