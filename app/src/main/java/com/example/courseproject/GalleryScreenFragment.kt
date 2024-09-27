package com.example.courseproject

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.courseproject.databinding.GalleryScreenFragmentBinding
import java.io.File

class GalleryScreenFragment : Fragment() {

    private lateinit var binding: GalleryScreenFragmentBinding

    private val cameraPermission = android.Manifest.permission.CAMERA
    private val cameraPermissionRequestCode = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GalleryScreenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val directory = File(requireContext().externalMediaDirs[0].absolutePath)
        val files = directory.listFiles() as Array<File>

        val adapter = GalleryAdapter(getAllMediaFiles())
        binding.recyclerForImage.adapter = adapter

        binding.buttonGoToPhotoCamera.setOnClickListener {
            checkCameraPermissionAndNavigateToPhotoScreen()
        }

        binding.buttonGoToVideoCamera.setOnClickListener {
            findNavController().navigate(R.id.videoCreationScreenFragment)
        }
    }

    private fun getAllMediaFiles(): ArrayList<String> {
        val mediaList = arrayListOf<String>()
        val cursor = requireContext().contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            null,
            null,
            null,
            "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        )

        if (cursor != null) {
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val data = cursor.getString(dataIndex)
                mediaList.add(data)
            }
            cursor.close()
        }
        return mediaList
    }

    private fun checkCameraPermissionAndNavigateToPhotoScreen() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(requireContext(), cameraPermission) -> {
                navigateToPhotoScreen()
            }

            else -> {
                requestCameraPermission()
            }
        }
    }

    private fun navigateToPhotoScreen() {
        findNavController().navigate(R.id.photoCreationScreenFragment)
    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(cameraPermission), cameraPermissionRequestCode)
    }
}
