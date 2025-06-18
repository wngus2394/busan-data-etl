package com.busan.dataetl.common.dto.file.response

data class GetFileResponse(
    val bucketName: String,
    val filePath: String,
    val fileSource: String?,
    val fileName: String,
    val fileSize: Int,
    val fileRating: Int
)
