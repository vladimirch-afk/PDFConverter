package com.example.pdfconverter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
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
    private var tmpBitmap : Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

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
            setImageBW(bwButton, colorButton)
        }
        colorButton.setOnClickListener {
            setImageToColor(bwButton, colorButton)
        }
        bwButton.setBackgroundColor(getResources().getColor(R.color.light_gray))
        colorButton.setBackgroundColor(getResources().getColor(R.color.bright_gray))
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        bwButton.width = displayMetrics.widthPixels / 2
//        colorButton.width = displayMetrics.widthPixels / 2


        currBitmap = android.graphics.BitmapFactory.decodeFile(address)
        cropImageView!!.setImageBitmap(currBitmap)
        startCrop()
    }

    private fun startCrop() {
        startActivityForResult(CropImage.activity(Uri.fromFile(File(address.toString())))
            .setGuidelines(CropImageView.Guidelines.ON)
            //.setAspectRatio(1920, 1080)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .getIntent(this),
            CROP_IMAGE_ACTIVITY_REQUEST_CODE)
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
        currBitmap = android.graphics.BitmapFactory.decodeFile(uri?.path)
        cropImageView!!.setImageBitmap(currBitmap)
    }

    private fun setImageBW(bw : Button, col : Button) {
        bw.setBackgroundColor(getResources().getColor(R.color.bright_gray))
        col.setBackgroundColor(getResources().getColor(R.color.light_gray))
        if (tmpBitmap == null) {
            var bitmap = currBitmap
            tmpBitmap = currBitmap
            bitmap = toGrayscale(bitmap!!)
            currBitmap = bitmap
            cropImageView!!.setImageBitmap(bitmap)
        }
    }

    private fun setImageToColor(bw : Button, col : Button) {
        bw.setBackgroundColor(getResources().getColor(R.color.light_gray))
        col.setBackgroundColor(getResources().getColor(R.color.bright_gray))
        if (tmpBitmap != null) {
            currBitmap = tmpBitmap
            tmpBitmap = null
        }
        cropImageView!!.setImageBitmap(currBitmap)
    }

    private fun saveImage() {
        Toast.makeText(this, "Сохранение...", Toast.LENGTH_LONG).show()
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