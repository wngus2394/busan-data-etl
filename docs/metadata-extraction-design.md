# 메타데이터 추출 파라미터 체계 설계 문서

## 1. 개요
본 문서는 부산 데이터 ETL 시스템의 메타데이터 추출 기능을 위한 파라미터 체계 설계를 설명합니다.

## 2. 설계 목표
- 유연한 메타데이터 필드 선택 지원
- CSV 출력 형식 커스터마이징 지원
- 기존 API와의 호환성 유지
- 강력한 유효성 검증

## 3. 구성 요소

### 3.1 MetadataField Enum
```kotlin
enum class MetadataField(
    val csvHeaderName: String,
    val description: String
)
```

**지원 필드:**
- `FILE_PATH`: 파일경로 - 오브젝트 스토리지 내 파일의 전체 경로
- `FILE_SOURCE`: 파일출처 - 파일이 속한 버킷명 또는 출처
- `FILE_NAME`: 파일명 - 파일의 이름 (확장자 포함)
- `FILE_EXTENSION`: 파일확장자 - 파일의 확장자 (점 제외)
- `FILE_SIZE`: 파일크기 - 파일의 크기 (바이트 단위)
- `FILE_HASH`: 파일해시 - 파일의 해시값 (SHA-256)
- `FILE_RATING`: 파일등급 - 파일의 등급 정보
- `UPLOAD_DATETIME`: 업로드일시 - 파일이 업로드된 일시

### 3.2 MetadataExtractionParams DTO
```kotlin
data class MetadataExtractionParams(
    val fields: List<MetadataField>,
    val includeHeader: Boolean = true,
    val dateFormat: String = "yyyy-MM-dd HH:mm:ss",
    val delimiter: String = ","
)
```

**파라미터 설명:**
- `fields`: 추출할 메타데이터 필드 목록 (필수, 최소 1개)
- `includeHeader`: CSV 헤더 포함 여부 (기본값: true)
- `dateFormat`: 날짜 형식 패턴 (기본값: "yyyy-MM-dd HH:mm:ss")
- `delimiter`: CSV 구분자 (기본값: ",")

### 3.3 검증 로직
`MetadataExtractionValidator` 클래스를 통한 파라미터 검증:

**검증 항목:**
1. 필드 선택 검증
   - 최소 1개 이상의 필드 선택 필수
   - 중복 필드 검사

2. 날짜 형식 검증
   - DateTimeFormatter 패턴 유효성 검사
   - 권장 형식: yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd, yyyyMMdd

3. 구분자 검증
   - 단일 문자 여부 확인
   - 특수 문자 사용 시 경고 (\n, \r, \t)

## 4. 기존 DTO 통합

### 4.1 AddUploadFileRequest 확장
```kotlin
data class AddUploadFileRequest(
    val bucketName: String,
    val metaExtractStatus: Boolean,
    val metaSaveFilePath: String? = null,
    val metadataExtractionParams: MetadataExtractionParams? = null
)
```

### 4.2 AddManualUploadFileRequest 확장
```kotlin
data class AddManualUploadFileRequest(
    val bucketName: String,
    val fileList: List<FileItem>,
    val metaExtractStatus: Boolean,
    val metaSaveFilePath: String? = null,
    val metadataExtractionParams: MetadataExtractionParams? = null
)
```

## 5. 사용 예시

### 5.1 기본 사용 (모든 필드, 기본 설정)
```json
{
  "bucketName": "test-bucket",
  "metaExtractStatus": true,
  "metaSaveFilePath": "./output/metadata.csv"
}
```

### 5.2 선택적 필드 추출
```json
{
  "bucketName": "test-bucket",
  "metaExtractStatus": true,
  "metaSaveFilePath": "./output/metadata.csv",
  "metadataExtractionParams": {
    "fields": ["FILE_PATH", "FILE_NAME", "FILE_SIZE", "UPLOAD_DATETIME"],
    "includeHeader": true,
    "dateFormat": "yyyy-MM-dd HH:mm:ss",
    "delimiter": ","
  }
}
```

### 5.3 커스텀 형식
```json
{
  "bucketName": "test-bucket",
  "metaExtractStatus": true,
  "metaSaveFilePath": "./output/metadata.tsv",
  "metadataExtractionParams": {
    "fields": ["FILE_NAME", "FILE_SIZE"],
    "includeHeader": false,
    "delimiter": "\t"
  }
}
```

## 6. 하위 호환성
- `metadataExtractionParams`가 null인 경우, 기존 동작 유지 (모든 필드 추출)
- 기존 API 호출은 변경 없이 작동

## 7. 확장 가능성
- 새로운 메타데이터 필드 추가 용이 (enum 확장)
- 출력 형식 확장 가능 (JSON, XML 등)
- 필터링 옵션 추가 가능

## 8. 성능 고려사항
- 필드 선택을 통한 메모리 사용량 최적화
- 대용량 파일 처리 시 선택적 필드 추출로 성능 향상

## 9. 보안 고려사항
- 파일 경로 노출 제어 (FILE_PATH 필드 선택적 사용)
- 해시값 포함 여부 제어 (FILE_HASH 필드 선택적 사용)