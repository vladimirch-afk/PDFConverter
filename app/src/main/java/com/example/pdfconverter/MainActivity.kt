package com.example.pdfconverter

//import com.itextpdf.text.Document
//import com.itextpdf.text.Image
//import com.itextpdf.text.pdf.PdfDocument
//import com.itextpdf.text.pdf.PdfWriter
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {


    companion object {
        private var images = mutableListOf<MyImage>()
        private var imageToSave : MyImage? = null
        fun addImage(image : MyImage) {
            images.add(image)
        }
    }
    private val entries = 6
    private lateinit var phoneNum: MutableList<String>
    private lateinit var buttonLabels: MutableList<String>
    lateinit var adapter: CustomRecyclerAdapter
    private var lastPickedDirectory: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        phoneNum = mutableListOf()
        buttonLabels = mutableListOf()

        loadData()
        val layoutManager = LinearLayoutManager(this)
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

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    fun deleteImage(image : String, ctx : Context) {
        System.gc()
        val img = images.find { it.name == image }
        for (item in img!!.images) {
            val file = File(item)
            file.delete()
            if(file.exists()) {
                file.canonicalFile.delete()
                if (file.exists()) {
                    ctx.deleteFile(item)
                }
            }
        }
        images.removeIf { it.name == image }
    }

    override fun onPause() {
        super.onPause()
        val jsonList = Json.encodeToString(images)
        val saveFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "save.txt")
        saveFile.writeText(jsonList)
    }
    fun loadNewImageActivity() {
        val intent = Intent(this, NewImageActivity::class.java)
        startActivity(intent)
    }

    fun exportToPDF(image : MyImage) {
        openDirectoryPicker()
        imageToSave = image
        println(image!!.images)
    }

    private fun loadData() {
        val saveFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "save.txt")
        if (!saveFile.exists()) {
            saveFile.createNewFile()
            saveFile.writeText("[]")
        }
        var lines = saveFile.readLines().joinToString()
        if (lines.isEmpty()) {
            saveFile.writeText("[]")
            lines = saveFile.readLines().joinToString()
        }
        println(lines)
        images = if (lines != null) {
            val obj = Json.decodeFromString<MutableList<MyImage>>(lines)
            obj
        } else {
            mutableListOf()
        }
    }

    private fun createPDFWithMultipleImage(image: MyImage, path: Uri?) {
        val file = File(path.toString())
        println(file)
        if (file != null) {
            try {
                val fileOutputStream = FileOutputStream(contentResolver
                    .openFileDescriptor(path!!, "w")!!.fileDescriptor)
                val pdfDocument = PdfDocument()
                for (i in 0 until image.images.size) {
                    val bitmap = BitmapFactory.decodeFile(image.images[i])
                    val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(
                        maxOf(2480, bitmap.width),
                        maxOf(3508, bitmap.height),
                        i + 1
                    ).create()
                    val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
                    val canvas: Canvas = page.getCanvas()
                    val paint = Paint()
                    paint.color = Color.WHITE
                    canvas.drawPaint(paint)
                    val leftOffset = (2400 - bitmap.width) / 2
                    val topOffset = (3500 - bitmap.height) / 2
                    canvas.drawBitmap(bitmap, leftOffset.toFloat(), topOffset.toFloat(), null)
                    pdfDocument.finishPage(page)
                    bitmap.recycle()
                }
                pdfDocument.writeTo(fileOutputStream)
                pdfDocument.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        println(images.toString())
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
        println(images.toString())
    }

    private val pickDirectory = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val pickedDir = DocumentFile.fromTreeUri(this, uri)
                val file = pickedDir?.createFile("application/pdf", imageToSave!!.name)
                lastPickedDirectory = pickedDir?.uri
                println(pickedDir?.uri)
                println(pickedDir)
                createPDFWithMultipleImage(imageToSave!!, file!!.uri)
            }
        }
    }

    private fun openDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        pickDirectory.launch(intent)
    }

}

