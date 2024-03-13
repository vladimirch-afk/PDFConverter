package com.example.pdfconverter

//import com.itextpdf.text.Document
//import com.itextpdf.text.Image
//import com.itextpdf.text.pdf.PdfDocument
//import com.itextpdf.text.pdf.PdfWriter
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        private var imageToSave : MyImage? = null
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
    private var lastPickedDirectory: Uri? = null


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

    override fun onPause() {
        super.onPause()
        val file = File(R.raw.)
    }
    fun loadNewImageActivity() {
        val intent = Intent(this, NewImageActivity::class.java)
        startActivity(intent)
    }

    fun exportToPDF(image : MyImage) {
        openDirectoryPicker()
        imageToSave = image
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

    private fun createPDFWithMultipleImage(image: MyImage, path: Uri?) {
        val file = File(path.toString())
        println(file)
        if (file != null) {
            try {
                val fileOutputStream = FileOutputStream(contentResolver
                    .openFileDescriptor(path!!, "w")!!.fileDescriptor)
                val pdfDocument = PdfDocument()
                for (i in 0 until images.size) {
                    val bitmap = BitmapFactory.decodeFile(image.images[i])
                    val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(
                        2400,
                        3500,
                        i + 1
                    ).create()
                    val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
                    val canvas: Canvas = page.getCanvas()
                    val paint = Paint()
                    paint.color = Color.WHITE
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

//    private fun selectDirectory() : String? {
//        var p : String? = null
//        val chooser: StorageChooser = Builder() // Specify context of the dialog
//            .withActivity(this@MainActivity)
//            .withFragmentManager(fragmentManager)
//            .withMemoryBar(true)
//            .allowCustomPath(true) // Define the mode as the FOLDER/DIRECTORY CHOOSER
//            .setType(StorageChooser.DIRECTORY_CHOOSER)
//            .build()
//        // 2. Handle what should happened when the user selects the directory !
//        chooser.setOnSelectListener(object : OnSelectListener() {
//            fun onSelect(path: String?) {
//                p = path
//            }
//        })
//        // 3. Display File Picker whenever you need to !
//        chooser.show()
//        return p
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 100) {
//        }
//    }

    private val createDocument = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        uri?.let {
            // Now you can use the DocumentFile to save your file in the chosen directory
            val documentFile = DocumentFile.fromSingleUri(this, it)
            saveFileInDirectory(documentFile?.uri)
        }
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
                // Now you have the directory path
                // Use it as needed
            }
        }
    }

    private fun openDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        // Optionally, you can set a starting directory using setInitialUri for Android 11 and above

        pickDirectory.launch(intent)
    }

    private fun saveFileInDirectory(path : Uri?) {

    }

}

//class MainActivity : AppCompatActivity() {
//
//    private val REQUEST_CODE_PICK_DIR = 123
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        button_save.setOnClickListener {
//            openDirectoryPicker()
//        }
//    }
//
//    private fun openDirectoryPicker() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//        startActivityForResult(intent, REQUEST_CODE_PICK_DIR)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CODE_PICK_DIR && resultCode == Activity.RESULT_OK) {
//            data?.data?.let { uri ->
//                val pickedDir = DocumentFile.fromTreeUri(this, uri)
//                val file = pickedDir?.createFile("text/plain", "example.txt")
//                file?.uri?.let { fileUri ->
//                    saveFileContent(fileUri)
//                }
//            }
//        }
//    }
//
//    private fun saveFileContent(uri: android.net.Uri) {
//        contentResolver.openFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
//            val outputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
//            outputStream.write("Hello, this is the content of the file.".toByteArray())
//            outputStream.close()
//        }
//    }
//}
