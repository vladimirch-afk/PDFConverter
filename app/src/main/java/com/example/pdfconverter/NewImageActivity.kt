package com.example.pdfconverter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File


class NewImageActivity : AppCompatActivity() {

    private var imageCapture : ImageCapture? = null
    companion object {
        private var imagesName : String? = null
        private var image : MyImage? = null

        fun createImage() {
            imagesName = System.currentTimeMillis().toString()
            image = MyImage(imagesName!!, mutableListOf())
        }

        fun addImage(path : String) {
            image!!.images.add(path)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_image)

//        val previewView = findViewById<PreviewView>(R.id.cameraView)
//
//        // Set the preferred implementation mode before starting the preview
//        previewView.preferredImplementationMode = ImplementationMode.SURFACE_VIEW
//
//// Attach the preview use case and previewView to start the preview
//        preview.setSurfaceProvider(previewView.createSurfaceProvider(cameraInfo))
//
//        // Create a preview use case instance
//        val preview = Preview.Builder().build()
//
//        // Bind the preview use case and other needed user cases to a lifecycle
//        val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis, imageCapture)
//
//        // Create a surfaceProvider using the bound camera's cameraInfo
//        val surfaceProvider = previewView.createSurfaceProvider(camera.cameraInfo)
//
//        // Attach the surfaceProvider to the preview use case to start preview
//        preview.setSurfaceProvider(surfaceProvider)
//
//
//        previewView.setScaleType(ScaleType.FIT_CENTER)
        createImage()

        val previewView = findViewById<PreviewView>(R.id.cameraView)

        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider, previewView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))

        val shotButton = findViewById<Button>(R.id.shotButton)
        val backButton = findViewById<Button>(R.id.backButton)

        shotButton.setOnClickListener {
            capturePhoto()
        }
        backButton.setOnClickListener {
            goBack()
        }

    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider, previewView: PreviewView) {
        val preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
    }

    private fun capturePhoto() {
        val name = System.currentTimeMillis().toString()
        val photoFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${name}.jpg")
        val outputFileOptions = OutputFileOptions.Builder(photoFile).build()
        imageCapture!!.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: OutputFileResults) {
                    // Photo captured and saved successfully
                    Toast.makeText(
                        this@NewImageActivity,
                        "Photo saved to " + photoFile.absolutePath,
                        Toast.LENGTH_SHORT
                    ).show()
                    launchCropActivity(photoFile.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    // Photo capture failed
                    Toast.makeText(
                        this@NewImageActivity,
                        exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
            }
        })
    }

    private fun goBack() {
        if (image!!.images.size > 0) {
            MainActivity.addImage(image!!)
        }
        image = null
        finish()
    }

    private fun launchCropActivity(address : String) {
        val intent = Intent(this, CropActivity::class.java)
        intent.putExtra("address", address)
        startActivity(intent)
    }

}