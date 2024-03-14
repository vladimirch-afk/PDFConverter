package com.example.pdfconverter

import kotlinx.serialization.Serializable

@Serializable
data class MyImage(
    var name: String,
    var date: String,
    var images: MutableList<String>,
)

