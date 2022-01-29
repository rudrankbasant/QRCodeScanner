package com.example.qrcodescanner

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*

private const val CAMERA_REQ_CODE = 101

class MainActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var scanner_view: CodeScannerView
    private lateinit var tv_textview: TextView
    private lateinit var mFlashButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        scanner_view = findViewById(R.id.scanner_view)
        tv_textview=findViewById(R.id.tv_textview)
        codeScan()
    }

    private fun codeScan() {
        codeScanner = CodeScanner(this, scanner_view)
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.CONTINUOUS// or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                tv_textview.text = it.text

                //Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }

    }


    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)

        if (permission!=PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
        arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQ_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQ_CODE -> {
                if (grantResults.isEmpty() || grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"You need camera permission to use this app!", Toast.LENGTH_SHORT).show()
                }else{
                    //successful
                }
            }
        }
    }
}