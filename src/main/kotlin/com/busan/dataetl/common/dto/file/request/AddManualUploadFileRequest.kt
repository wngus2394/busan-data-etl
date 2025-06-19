package com.busan.dataetl.common.dto.file.request

import com.busan.dataetl.common.dto.metadata.MetadataExtractionParams
import com.busan.dataetl.common.openapi.schema.BucketSchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

data class AddManualUploadFileRequest(
    @BucketSchema
    val bucketName: String,

    @Schema(description = "파일 리스트")
    val fileList: List<FileItem>,

    @Schema(description = "파일 메타정보 추출여부", example = "true")
    val metaExtractStatus: Boolean,

    @Schema(
        description = "메타정보 저장경로 (파일명포함)",
        nullable = true,
        example = "./output/filemeta.csv"
    )
    val metaSaveFilePath: String? = null,

    @field:Valid
    @Schema(
        description = "메타데이터 추출 파라미터",
        nullable = true
    )
    val metadataExtractionParams: MetadataExtractionParams? = null
) {

    data class FileItem(
        @Schema(description = "Storage 전체 경로", example = "품질_4.0등급/C기획관/02조직담당관/매뉴얼")
        val storageFullPath: String,

        @Schema(description = "파일명", example = "2207_조직담당관_업무매뉴얼_행정안전부_지방자치단체 위원회 정비지침 Q&A.pdf")
        val fileName: String
    )
}
