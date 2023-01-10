package com.akilincarslan.mlkit.textrecognizer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.akilincarslan.mlkit.BuildConfig
import com.akilincarslan.mlkit.databinding.FragmentTextRecognizerBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import java.io.File
import java.util.concurrent.Executor


class TextRecognizerFragment : Fragment() {
    private var binding :FragmentTextRecognizerBinding? =null
    private val startForResult = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            latestTempUri?.let { uri ->
                binding?.ivInput?.setImageURI(uri)
                binding?.btnExtract?.isVisible = true
            }

        }
    }
    private var latestTempUri :Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTextRecognizerBinding.inflate(inflater,container,false)
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
            btnExtract.setOnClickListener {
                progressBar.isVisible = true
                runTextRecognition()
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

    private fun runTextRecognition() {
        val image = InputImage.fromFilePath(requireContext(), latestTempUri!!)
        val recognizer = TextRecognition.getClient()
        recognizer.process(image)
            .addOnSuccessListener { texts ->
                binding?.progressBar?.isVisible = false
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener { e -> // Task failed with an exception
                binding?.progressBar?.isVisible = false
                Log.e("TextRecognizerFragment","Ex: $e")
            }
    }

    private fun processTextRecognitionResult(texts: Text) {
        if (texts.text.isEmpty()) {
            Snackbar.make(requireView(),"No text found!",Snackbar.LENGTH_LONG).show()
            return
        }
        binding?.tvResult?.text = texts.text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}