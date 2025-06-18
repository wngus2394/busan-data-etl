package com.busan.dataetl.common.dto.file

import com.busan.dataetl.common.extension.csvEscape

data class MetaData(
    val filePath: String,
    val fileSource: String?, //파일출처
    val fileName: String,
    val fileExtension: String,
    val fileSize: Int,
    val fileHash: String,
    val fileRating: Int, //파일등급
    val uploadDateTime: String?
) {

    /**
     * CSV용 ROW 변환
     */
    fun toCsvRow(): String {
        return listOf(
            filePath.csvEscape(),
            (fileSource ?: "").csvEscape(),
            fileName.csvEscape(),
            fileExtension.csvEscape(),
            fileSize.toString(),
            fileHash.csvEscape(),
            fileRating.toString(),
            uploadDateTime?.csvEscape()
        ).joinToString(",")
    }
}
