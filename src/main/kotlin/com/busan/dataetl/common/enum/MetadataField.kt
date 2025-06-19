package com.busan.dataetl.common.enum

enum class MetadataField(
    val csvHeaderName: String,
    val description: String
) {
    FILE_PATH("파일경로", "오브젝트 스토리지 내 파일의 전체 경로"),
    FILE_SOURCE("파일출처", "파일이 속한 버킷명 또는 출처"),
    FILE_NAME("파일명", "파일의 이름 (확장자 포함)"),
    FILE_EXTENSION("파일확장자", "파일의 확장자 (점 제외)"),
    FILE_SIZE("파일크기", "파일의 크기 (바이트 단위)"),
    FILE_HASH("파일해시", "파일의 해시값 (SHA-256)"),
    FILE_RATING("파일등급", "파일의 등급 정보"),
    UPLOAD_DATETIME("업로드일시", "파일이 업로드된 일시");

    companion object {
        fun fromString(value: String): MetadataField? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
        
        fun getAllHeaders(): List<String> {
            return values().map { it.csvHeaderName }
        }
    }
}