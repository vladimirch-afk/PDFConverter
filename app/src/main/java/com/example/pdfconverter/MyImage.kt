package com.example.pdfconverter


class MyImage(
    var name: String,
    var images: MutableList<String>,
) {
    override fun toString(): String {
        return "$name"
    }
}

