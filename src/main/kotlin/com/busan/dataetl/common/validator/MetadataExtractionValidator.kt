package com.busan.dataetl.common.validator

import com.busan.dataetl.common.dto.metadata.MetadataExtractionParams
import com.busan.dataetl.common.enum.MetadataField
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class MetadataExtractionValidator {
    
    fun validate(params: MetadataExtractionParams): ValidationResult {
        val errors = mutableListOf<String>()
        
        // 필드 선택 검증
        if (params.fields.isEmpty()) {
            errors.add("최소 1개 이상의 메타데이터 필드를 선택해야 합니다")
        }
        
        // 중복 필드 검증
        val duplicateFields = params.fields.groupBy { it }
            .filter { it.value.size > 1 }
            .keys
        
        if (duplicateFields.isNotEmpty()) {
            errors.add("중복된 필드가 있습니다: ${duplicateFields.joinToString { it.name }}")
        }
        
        // 날짜 형식 검증
        try {
            DateTimeFormatter.ofPattern(params.dateFormat)
        } catch (e: IllegalArgumentException) {
            errors.add("유효하지 않은 날짜 형식입니다: ${params.dateFormat}")
        }
        
        // 구분자 검증
        if (params.delimiter.isEmpty()) {
            errors.add("구분자는 비어있을 수 없습니다")
        } else if (params.delimiter.length > 1) {
            errors.add("구분자는 단일 문자여야 합니다")
        }
        
        // 특수 구분자 경고
        val warnings = mutableListOf<String>()
        if (params.delimiter in listOf("\n", "\r", "\t")) {
            warnings.add("특수 문자를 구분자로 사용하면 CSV 파싱에 문제가 발생할 수 있습니다")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
    
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList(),
        val warnings: List<String> = emptyList()
    )
    
    companion object {
        private val RECOMMENDED_DATE_FORMATS = listOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd",
            "yyyyMMdd"
        )
        
        fun getRecommendedDateFormats(): List<String> = RECOMMENDED_DATE_FORMATS
    }
}