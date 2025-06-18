package com.busan.dataetl.common.dto.file

data class S3Object(
    val bucketName: String,
    val filePath: String,
    val fileSource: String?, //파일출처
    val fileName: String,
    val fileSize: Int,
    val fileRating: Int, //파일등급
    val uploadDateTime: String?
)
