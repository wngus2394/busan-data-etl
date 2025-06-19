package com.busan.dataetl.common.dto.metadata

import com.busan.dataetl.common.enum.MetadataField
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

@Schema(description = "메타데이터 추출 파라미터")
data class MetadataExtractionParams(
    @field:NotEmpty(message = "최소 1개 이상의 필드를 선택해야 합니다")
    @Schema(
        description = "추출할 메타데이터 필드 목록",
        example = "[\"FILE_PATH\", \"FILE_NAME\", \"FILE_SIZE\", \"UPLOAD_DATETIME\"]",
        required = true
    )
    val fields: List<MetadataField>,

    @Schema(
        description = "CSV 헤더 포함 여부",
        example = "true",
        defaultValue = "true"
    )
    val includeHeader: Boolean = true,

    @Schema(
        description = "날짜 형식 (Java DateTimeFormatter 패턴)",
        example = "yyyy-MM-dd HH:mm:ss",
        defaultValue = "yyyy-MM-dd HH:mm:ss"
    )
    @field:Pattern(
        regexp = "^[yMdHmsS\\-/: .]+$",
        message = "유효하지 않은 날짜 형식입니다"
    )
    val dateFormat: String = "yyyy-MM-dd HH:mm:ss",

    @Schema(
        description = "CSV 구분자",
        example = ",",
        defaultValue = ","
    )
    val delimiter: String = ","
) {
    init {
        require(fields.isNotEmpty()) { "최소 1개 이상의 필드를 선택해야 합니다" }
        require(delimiter.length == 1) { "구분자는 단일 문자여야 합니다" }
    }
    
    fun getSelectedHeaders(): List<String> {
        return fields.map { it.csvHeaderName }
    }
    
    fun containsField(field: MetadataField): Boolean {
        return fields.contains(field)
    }
}