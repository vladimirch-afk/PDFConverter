package com.example.pdfconverter

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CustomRecyclerAdapter (
    context: Context?,
    private val images: MutableList<MyImage>
) :
    RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
    private val ctx: Context?

    init {
        inflater = LayoutInflater.from(context)
        ctx = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        holder.name.text = image.name
        holder.date.text = image.date

        val imagePath = image.images[0]
        val file = java.io.File(imagePath)
        if (file.exists()) {
            // Use a library like Glide or Picasso for efficient image loading
            // In this example, we'll use the built-in BitmapFactory
            val options = android.graphics.BitmapFactory.Options()
            options.inJustDecodeBounds = true
            options.inSampleSize = calculateImageSize(options, 100, 100)
            options.inJustDecodeBounds = false
            val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            holder.image.setImageBitmap(bitmap)
        } else {
            // Handle the case when the file does not exist
            // You can show an error message or set a default image
        }
        holder.button1.setOnClickListener {
            (ctx as MainActivity).deleteImage(image.name, ctx)
            ctx.adapter.notifyDataSetChanged()
        }
        holder.button2.setOnClickListener {
            (ctx as MainActivity).exportToPDF(image)
        }
        holder.name.setOnClickListener {
            val builder = AlertDialog.Builder(ctx)
            builder.setTitle("Имя:")

            val input = EditText(ctx)
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.hint = image.name
            builder.setView(input)

            builder.setPositiveButton(
                "OK"
            ) { dialog, which -> image.name = if (input.text.toString().isNotEmpty())
            {
                input.getText().toString()
            }
            else "NO_NAME";
                (ctx as MainActivity).adapter.notifyDataSetChanged();
                dialog.cancel()}
            builder.setNegativeButton(
                "Отмена"
            ) { dialog, which ->  dialog.cancel()}
            builder.show()
        }
    }

    private fun calculateImageSize(options : android.graphics.BitmapFactory.Options,
                                   reqHeight : Int, reqWidth : Int) : Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val date: TextView
        val image: ImageView
        val button1: ImageView
        val button2: ImageView

        init {
            name = view.findViewById(R.id.commentView)
            image = view.findViewById(R.id.imageView)
            button1 = view.findViewById(R.id.deleteButton)
            button2 = view.findViewById(R.id.exportButton)
            date = view.findViewById(R.id.dateText)
        }
    }
}

