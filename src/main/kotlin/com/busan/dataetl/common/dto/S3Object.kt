package com.busan.dataetl.common.dto

data class S3Object(
    val bucketName: String,
    val rootFolder: String,
    val filePath: String,
    val fileName: String,
    val fileSize: Int,
)
