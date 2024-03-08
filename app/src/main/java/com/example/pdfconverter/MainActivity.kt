package com.example.pdfconverter

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val document = Document()

        val directoryPath = Environment.getExternalStorageDirectory().toString()

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = Environment.getExternalStorageDirectory()
        File.createTempFile(
            "example",
            ".pdf",
            storageDir
        )

        PdfWriter.getInstance(
            document,
            FileOutputStream("$directoryPath/example.pdf")
        ) //  Change pdf's name.


        document.open()

        val image: Image =
            Image.getInstance("$directoryPath/example.jpg") // Change image's name and extension.


        val scaler: Float = (document.getPageSize().getWidth() - document.leftMargin()
                - document.rightMargin() - 0) / image.width * 100 // 0 means you have no indentation. If you have any, change it.

        image.scalePercent(scaler)
        image.setAlignment(Image.ALIGN_CENTER or Image.ALIGN_TOP)

        document.add(image)
        document.close()
    }
}