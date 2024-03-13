package com.example.pdfconverter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CropActivity : AppCompatActivity() {

    private var resultUri : Uri? = null
    private var cropImageView : ImageView? = null
    private var address : String? = null
    private var currBitmap : Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

//        // start picker to get image for cropping and then use the image in cropping activity
//        CropImage.activity()
//            .setGuidelines(CropImageView.Guidelines.ON)
//            .start(this);
//
//// start cropping activity for pre-acquired image saved on the device
//        CropImage.activity(imageUri)
//            .start(this);
//
//// for fragment (DO NOT use `getActivity()`)
//        CropImage.activity()
//            .start(getContext(), this);

        cropImageView = findViewById<ImageView>(R.id.cropImageView)
        address = intent.getStringExtra("address")

        val cropButton = findViewById<Button>(R.id.cropButton)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelCropButton)

        val bwButton = findViewById<Button>(R.id.bwButton)
        val colorButton = findViewById<Button>(R.id.colorButton)

        cropButton.setOnClickListener {
            startCrop()
        }
        saveButton.setOnClickListener {
            saveImage()
        }
        cancelButton.setOnClickListener {
            finish()
        }

        bwButton.setOnClickListener {
            setImageBW()
        }
        colorButton.setOnClickListener {
            setImageToColor()
        }

        //launchCrop(address.toString())
        //val cropped = cropImageView.croppedImage

        cropImageView!!.setImageBitmap(android.graphics.BitmapFactory.decodeFile(address))
        startCrop()
// or (prefer using uri for performance and better user experience)

// or
    }

    private fun startCrop() {
        startActivityForResult(CropImage.activity(Uri.fromFile(File(address.toString())))
            .setGuidelines(CropImageView.Guidelines.ON)
            //.setAspectRatio(1920, 1080)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .getIntent(this),
            CROP_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    private fun launchCrop(address : String) {
        // start picker to get image for cropping and then use the image in cropping activity
//        CropImage.activity()
//            .setGuidelines(CropImageView.Guidelines.ON)
//            .start(this);
//
//// start cropping activity for pre-acquired image saved on the device
//        CropImage.activity( Uri.fromFile( File(address)))
//            .start(this);

        CropImage.activity(Uri.fromFile(File(address)))
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1920, 1080)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .start(this)

// for fragment (DO NOT use `getActivity()`)
        //CropImage.activity().start(this);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                setImage(result.uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                println(error)
                setImage(null)
            }
        }
    }

    private fun setImage(uri : Uri?) {
        println(uri?.path)
        if (uri != null) {
            address = uri.path.toString()
        }
        currBitmap = android.graphics.BitmapFactory.decodeFile(address.toString())
        cropImageView!!.setImageBitmap(currBitmap)
    }

    private fun setImageBW() {
        var bitmap = android.graphics.BitmapFactory.decodeFile(address.toString())
        bitmap = toGrayscale(bitmap)
        currBitmap = bitmap
        cropImageView!!.setImageBitmap(bitmap)
    }

    private fun setImageToColor() {
        currBitmap = null
        cropImageView!!.setImageBitmap(android.graphics.BitmapFactory.decodeFile(address.toString()))
    }

    private fun saveImage() {
        if (currBitmap != null) {
            try {
                FileOutputStream(address).use { out ->
                    currBitmap!!.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        out
                    ) // bmp is your Bitmap instance
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        NewImageActivity.addImage(address.toString())
        finish()
    }

    fun toGrayscale(srcImage: Bitmap): Bitmap {
        val bmpGrayscale =
            Bitmap.createBitmap(srcImage.getWidth(), srcImage.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        paint.setColorFilter(ColorMatrixColorFilter(cm))
        canvas.drawBitmap(srcImage, 0f, 0f, paint)
        return bmpGrayscale
    }
}