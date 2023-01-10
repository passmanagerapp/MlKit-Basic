package com.akilincarslan.mlkit.barcode

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.akilincarslan.mlkit.BuildConfig
import com.akilincarslan.mlkit.R
import com.akilincarslan.mlkit.databinding.FragmentBarcodeScannerBinding
import com.akilincarslan.mlkit.databinding.FragmentFaceDetectorBinding
import com.akilincarslan.mlkit.databinding.FragmentTextRecognizerBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import java.io.File


class BarcodeScannerFragment : Fragment() {
    private var _binding : FragmentBarcodeScannerBinding? =null
    private val binding :FragmentBarcodeScannerBinding
    get() = _binding!!
    private val startForResult = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            latestTempUri?.let { uri ->
                binding.ivInput?.setImageURI(uri)
                binding.btnScan?.isVisible = true
            }

        }
    }
    private var latestTempUri : Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBarcodeScannerBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        binding?.apply {
            fabOpenCamera.setOnClickListener {
                openCamera()
            }
            btnScan.setOnClickListener {
                progressBar.isVisible = true
                scanBarcode()
            }
        }
    }

    private fun openCamera() {
        getTmpFileUri().apply {
            latestTempUri = this
            startForResult.launch(this)
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    private fun scanBarcode() {
        val scanner = BarcodeScanning.getClient()
        val image = InputImage.fromFilePath(requireContext(),latestTempUri!!)
        val result = scanner.process(image)
            .addOnSuccessListener { barcodes ->
                binding.progressBar.isVisible = false
                handleBarcodeResult(barcodes)
            }
            .addOnFailureListener {
                binding.progressBar.isVisible = false
            }
    }

    private fun handleBarcodeResult(barcodes: List<Barcode>) {
        if (barcodes.isEmpty())
            Snackbar.make(requireView(),"No barcode found!",Snackbar.LENGTH_LONG).show()
        for (barcode in barcodes) {
            val bounds = barcode.boundingBox
            val corners = barcode.cornerPoints
            val rawValue = barcode.rawValue

            when (barcode.valueType) {
                Barcode.TYPE_WIFI -> {
                    val ssid = barcode.wifi!!.ssid
                    val password = barcode.wifi!!.password
                    val type = barcode.wifi!!.encryptionType
                    binding.tvResult.text = "$ssid $password $type"
                }
                Barcode.TYPE_URL -> {
                    val title = barcode.url!!.title
                    val url = barcode.url!!.url
                    binding.tvResult.text = "$title $url"
                }
                else -> {
                    binding.tvResult.text = barcode.displayValue + barcode.valueType.toString()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}