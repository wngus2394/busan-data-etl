package com.busan.dataetl.common.dto.file.response

data class GetFileResponse(
    val bucketName: String,
    val rootFolder: String,
    val filePath: String,
    val fileName: String,
    val fileSize: Int
)
