package com.busan.dataetl.common.dto.file.request

import com.busan.dataetl.common.dto.metadata.MetadataExtractionParams
import com.busan.dataetl.common.openapi.schema.BucketSchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

data class AddUploadFileRequest(
    @BucketSchema
    val bucketName: String,

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
)
