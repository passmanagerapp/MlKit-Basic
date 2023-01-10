package com.akilincarslan.mlkit.facedetector

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.akilincarslan.mlkit.databinding.FragmentFaceDetectorBinding
import com.akilincarslan.mlkit.utils.FaceContourGraphic
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions


class FaceDetectorFragment : Fragment() {

    private var _binding : FragmentFaceDetectorBinding? =null
    private val binding :FragmentFaceDetectorBinding
        get() = _binding!!

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            binding.ivInput.setImageURI(it.data?.data)
            binding.btnDetect.isVisible = true
            latestTempUri = it.data?.data
        }
    }
    private var latestTempUri : Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFaceDetectorBinding.inflate(inflater,container,false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        binding.apply {
            fabOpenGallery.setOnClickListener {
                openCamera()
            }
            btnDetect.setOnClickListener {
                progressBar.isVisible = true
                runFaceContourDetection()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForResult.launch(intent)
    }

    private fun runFaceContourDetection() {
        val image = InputImage.fromFilePath(requireContext(), latestTempUri!!)
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()
        val detector: FaceDetector = FaceDetection.getClient(options)
        detector.process(image)
            .addOnSuccessListener { faces->
                binding.progressBar.isVisible = false
                processFaceContourDetectionResult(faces)
            }
            .addOnFailureListener { e -> // Task failed with an exception
                binding.progressBar.isVisible = false
                Log.e("FaceDetectorFragment","Exception $e")
            }
    }

    private fun processFaceContourDetectionResult(faces: List<Face>) {
        // Task completed successfully
        if (faces.isEmpty()) {
            Snackbar.make(requireView(),"No face found",Snackbar.LENGTH_LONG).show()
            return
        }
        binding.graphicOverlay.clear()
        for (i in faces.indices) {
            val face: Face = faces[i]
            val faceGraphic = FaceContourGraphic(binding.graphicOverlay)
            binding.graphicOverlay.add(faceGraphic)
            faceGraphic.updateFace(face)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}