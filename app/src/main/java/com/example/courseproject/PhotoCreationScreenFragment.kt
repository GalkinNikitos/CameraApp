package com.example.courseproject

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.courseproject.databinding.PhotoCreationScreenFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoCreationScreenFragment : Fragment() {

    private var _binding : PhotoCreationScreenFragmentBinding? =  null
    private val binding get() = _binding!!
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imageCaptureExecutor: ExecutorService

    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                startCamera()
            } else {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "The camera permission is necessary",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.preview.surfaceProvider)
        }
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.d("TAG", "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PhotoCreationScreenFragmentBinding.inflate(inflater, container, false)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imageCaptureExecutor = Executors.newSingleThreadExecutor()
        cameraPermissionResult.launch(android.Manifest.permission.CAMERA)

        binding.buttonGoToVideo.setOnClickListener {
            findNavController().navigate(R.id.videoCreationScreenFragment)
        }
        binding.buttonGoToGallery.setOnClickListener {
            findNavController().navigate(R.id.galleryScreenFragment)
        }
        binding.buttonTakeAPhoto.setOnClickListener {
            takePhoto()
            animateFlash()
        }
        binding.buttonTurnACamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        imageCaptureExecutor.shutdown()
    }

    private fun takePhoto() {
        imageCapture?.let {
            val fileName = "PNG_${System.currentTimeMillis()}.png"
            val file = File(requireContext().externalMediaDirs[0], fileName)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            it.takePicture(
                outputFileOptions,
                imageCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                        val savedUri = outputFileResults.savedUri ?: Uri.fromFile(file)
//                        requireActivity().contentResolver.notifyChange(savedUri, null)
//                        Log.i("TAG", "The image has been saved in ${savedUri.toString()}")
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            requireContext(),
                            "Error taking photo",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("TAG", "Error taking photo:$exception")
                    }
                }
            )
        }
    }


    private fun animateFlash() {
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }
}