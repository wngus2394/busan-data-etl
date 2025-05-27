package com.busan.dataetl.common.dto.file.request

data class AddManualUploadFileRequest(
    val bucketName: String,
    val fileList: List<FileItem>
) {

    data class FileItem(
        val storageFullPath: String,
        val fileName: String
    )
}
