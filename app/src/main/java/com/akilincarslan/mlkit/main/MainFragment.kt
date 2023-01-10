package com.akilincarslan.mlkit.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.akilincarslan.mlkit.R
import com.akilincarslan.mlkit.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private var binding :FragmentMainBinding? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        binding?.apply {
            btnTextRecognizer.setOnClickListener {
                findNavController().navigate(R.id.action_fragment_main_to_fragment_text_recognazier)
            }
            btnFacialDetector.setOnClickListener {
                findNavController().navigate(R.id.action_fragment_main_to_faceDetectorFragment)
            }
            btnBarcodeScanner.setOnClickListener {
                findNavController().navigate(R.id.action_fragment_main_to_barcodeScannerFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}