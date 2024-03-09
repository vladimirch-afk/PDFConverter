package com.example.pdfconverter

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.itextpdf.text.Document
//import com.itextpdf.text.Image
//import com.itextpdf.text.pdf.PdfDocument
//import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.pdf.PdfDocument
import android.os.Environment


class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val document = Document()
//
//        val directoryPath = Environment.getExternalStorageDirectory().toString()
//
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = Environment.getExternalStorageDirectory()
//        File.createTempFile(
//            "example",
//            ".pdf",
//            storageDir
//        )
//
//        PdfWriter.getInstance(
//            document,
//            FileOutputStream("$directoryPath/example.pdf")
//        ) //  Change pdf's name.
//
//
//        document.open()
//
//        val image: Image =
//            Image.getInstance("$directoryPath/example.jpg") // Change image's name and extension.
//
//
//        val scaler: Float = (document.getPageSize().getWidth() - document.leftMargin()
//                - document.rightMargin() - 0) / image.width * 100 // 0 means you have no indentation. If you have any, change it.
//
//        image.scalePercent(scaler)
//        image.setAlignment(Image.ALIGN_CENTER or Image.ALIGN_TOP)
//
//        document.add(image)
//        document.close()
//    }

    companion object {
        private val images = mutableListOf<MyImage>()
        fun addImage(image : MyImage) {
            images.add(image)
        }

        fun deleteImage(image : String) {
            images.removeIf { it.name == image }
        }
    }
    private val entries = 6
    private lateinit var phoneNum: MutableList<String>
    private lateinit var buttonLabels: MutableList<String>
    lateinit var adapter: CustomRecyclerAdapter

//    fun populateArrays() {
//        phoneNum.add("+7(930)839-31-89")
//        phoneNum.add("+7(965)325-04-57")
////        phoneNum[2] = "345-678-90-12"
////        phoneNum[3] = "456-789-01-23"
////        phoneNum[4] = "567-890-12-34"
////        phoneNum[5] = "678-901-23-45"
//        buttonLabels.add("Чечуров Владимир")
//        buttonLabels.add("Друг1")
////        buttonLabels[2] = "Семеныч Сеня"
////        buttonLabels[3] = "Кузнецова Катя"
////        buttonLabels[4] = "Смирнова Саша"
////        buttonLabels[5] = "Попова Полина"
//        images.add(Image("Чечуров Владимир", "+7(930)839-31-89"))
//        images.add(Image("Друг1", "+7(965)325-04-57"))
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        phoneNum = mutableListOf()
        buttonLabels = mutableListOf()
        //populateArrays()

        val layoutManager = LinearLayoutManager(this);

        val recyclerView = findViewById<RecyclerView>(R.id.review1)
        adapter = CustomRecyclerAdapter(this, images)
        recyclerView.layoutManager = layoutManager;
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        val newContact = findViewById<Button>(R.id.newImage)
        newContact.setOnClickListener {
            loadNewImageActivity()
        }




    }

//    override fun onClick(v: View?) {
//        when (v!!.id) {
//            R.id.button1 -> launchDialer(phoneNum[0])
//            R.id.button2 -> launchDialer(phoneNum[1])
//        }
//
//    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }


    fun launchDialer(number : String) {
        val numberToDial = "tel:" + number
        startActivity(Intent (Intent.ACTION_DIAL, Uri.parse(numberToDial)))
    }


    fun loadNewImageActivity() {
        val intent = Intent(this, NewImageActivity::class.java)
        startActivity(intent)
    }

    fun exportToPDF(image : MyImage) {
        val imagePaths = image.images
        createPDFWithMultipleImage(image)


//        // Replace "output.pdf" with the desired output PDF file name
//        val outputPdfFilePath = Environment.getExternalStorageDirectory().absolutePath + "/${image.name}.pdf"
//
//        // Generate the PDF file from the list of image paths
//        generatePdfFromImages(imagePaths, outputPdfFilePath)
    }

    private fun generatePdfFromImages(imagePaths: MutableList<String>, outputFilePath: String) {
//        GlobalScope.launch(Dispatchers.IO) {
//            val pdfWrite = PdfWriter(FileOutputStream(outputFilePath))
//            val pdfWriter = PdfWriter(FileOutputStream(outputFilePath))
//            val pdfDocument = PdfDocument(pdfWriter)
//            val document = Document(pdfDocument)
//
//            for (imagePath in imagePaths) {
//                val image = Image(ImageDataFactory.create(imagePath))
//                document.add(image)
//            }
//
//            document.close()
//        }
    }

    private fun createPDFWithMultipleImage(image: MyImage) {
        val file = getOutputFile()
        if (file != null) {
            try {
                val fileOutputStream = FileOutputStream(file)
                val pdfDocument = PdfDocument()
                for (i in 0 until images.size) {
                    val bitmap = BitmapFactory.decodeFile(image.images[i])
                    val pageInfo: android.graphics.pdf.PdfDocument.PageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(
                        bitmap.width,
                        bitmap.height,
                        i + 1
                    ).create()
                    val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
                    val canvas: Canvas = page.getCanvas()
                    val paint = Paint()
                    paint.color = Color.BLUE
                    canvas.drawPaint(paint)
                    canvas.drawBitmap(bitmap, 0f, 0f, null)
                    pdfDocument.finishPage(page)
                    bitmap.recycle()
                }
                pdfDocument.writeTo(fileOutputStream)
                pdfDocument.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getOutputFile(): File? {
        var isFolderCreated = true
        return if (isFolderCreated) {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFileName = "PDF_$timeStamp"
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$imageFileName.pdf")
        } else {
            Toast.makeText(this, "Folder is not created", Toast.LENGTH_SHORT).show()
            null
        }
    }

}