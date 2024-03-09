package com.example.pdfconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CropActivity : AppCompatActivity() {
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
    }
}