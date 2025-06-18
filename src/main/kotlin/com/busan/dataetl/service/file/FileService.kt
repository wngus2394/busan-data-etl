package com.busan.dataetl.service.file

import com.busan.dataetl.api.UploaderAPI
import com.busan.dataetl.common.dto.PageableResponse
import com.busan.dataetl.common.dto.file.MetaData
import com.busan.dataetl.common.dto.file.S3Object
import com.busan.dataetl.common.dto.file.request.AddManualUploadFileRequest
import com.busan.dataetl.common.dto.file.request.AddUploadFileRequest
import com.busan.dataetl.common.dto.file.request.GetFileRequest
import com.busan.dataetl.common.dto.file.response.GetFileResponse
import com.busan.dataetl.common.extension.extractRootFolder
import com.busan.dataetl.common.extension.extractSource
import com.busan.dataetl.common.extension.safeToInt
import com.busan.dataetl.common.function.validateParams
import com.busan.dataetl.common.type.SecurityLevel
import com.busan.dataetl.util.CsvUtil
import com.busan.dataetl.util.FileUtil
import com.busan.dataetl.util.StorageUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

/**
 * 파일 기능 처리 서비스 영역
 * 파일과 관련된 모든 기능들을 수행하고 처리하는 로직들이 정의된 클래스이다.
 */
@Service
class FileService {

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    /**
     * 파일 리스트 조회
     *
     * @param request 요청 항목 객체
     * @return 파일 정보 리스트
     * {@link common.dto.file.response#GetFileResponse}
     */
    fun findFileList(request: GetFileRequest): PageableResponse<List<GetFileResponse>> = with(request) {
        // 파라미터 체크
        validateParams(bucketName)

        val fileList = StorageUtil.getObjectList(bucketName)
        val result = fileList.map {
            GetFileResponse(
                bucketName = bucketName,
                filePath = it.filePath,
                fileSource = it.fileSource,
                fileName = it.fileName,
                fileSize = it.fileSize,
                fileRating = it.fileRating
            )
        }

        PageableResponse(
            totalPageCount = fileList.size,
            data = result
        )
    }

    /**
     * 파일 업로드
     *
     * @param request 요청 항목 객체
     */
    fun addUploadFile(request: AddUploadFileRequest) = with(request) {
        // 파라미터 체크
        validateParams(bucketName)

        val fileList = StorageUtil.getObjectList(bucketName)
        val metadataList = mutableListOf<MetaData>()
        var failCount = 0

        fileList.forEach { file ->
            runCatching {
                val metadata = processFile(bucketName, file)
                metadata?.let { metadataList += it }
            }.onFailure { e ->
                logger.error("File processing failed >> {}/{} - {}", file.filePath, file.fileName, e.localizedMessage)
                failCount++
            }
        }

        // 메타데이터 추출
        if (metaExtractStatus) {
            saveToMetadataFile(metadataList, metaSaveFilePath)
        }

        // 실패 처리 파일 확인
        if (failCount > 0) {
            logger.warn("All files processed. Total: {}, Failed: {}", fileList.size, failCount)
            throw IllegalStateException("파일 일부 처리 실패 (총 ${failCount}건). 로그를 참조하세요.")
        } else {
            logger.info("All files processed successfully. Total: {}", fileList.size)
        }
    }

    /**
     * 파일 수동 업로드
     *
     * @param request 요청 항목 객체
     */
    fun addManualUploadFile(request: AddManualUploadFileRequest) = with(request) {
        // 파라미터 체크
        validateParams(bucketName, fileList)

        val metadataList = mutableListOf<MetaData>()
        fileList.forEach { data ->
            runCatching {
                val fileMetadata = StorageUtil.getObjectMetadata(bucketName, data.storageFullPath, data.fileName)
                val fileSize = fileMetadata.contentLength.safeToInt()
                val fileSource = data.storageFullPath.extractSource()
                val securityCode = data.storageFullPath.extractRootFolder()
                    .substringAfterLast('_')
                val securityLevel = SecurityLevel.convert(securityCode)

                val fileObject = S3Object(
                    bucketName = bucketName,
                    filePath = data.storageFullPath,
                    fileSource = fileSource,
                    fileName = data.fileName,
                    fileSize = fileSize,
                    fileRating = securityLevel.level,
                    uploadDateTime = null
                )


                val metadata = processFile(bucketName, fileObject)
                metadata?.let { metadataList += it }
            }.onFailure { e ->
                logger.error("File processing failed >> {} - {}", data.fileName, e.localizedMessage)
                throw IllegalStateException("파일 처리 실패")
            }
        }

        // 메타데이터 추출
        if (metaExtractStatus) {
            saveToMetadataFile(metadataList, metaSaveFilePath)
        }
    }

    /**
     * 파일 수집·저장 처리
     *
     * @param bucketName 버킷명
     * @param file 파일 정보 객체
     * @return 메타데이터 객체
     */
    private fun processFile(bucketName: String, file: S3Object): MetaData? {
        return StorageUtil.tempFileDownload(bucketName, file.filePath, file.fileName) { tempFile ->
            val originalName = file.fileName
            val fileHash = FileUtil.calculateSHA256(tempFile.toPath())
            val fileExtension = file.fileName.substringAfterLast('.', "")
                .takeIf { it.isNotBlank() }
                ?.uppercase(Locale.ROOT)
                ?: "UNKNOWN"

            val duplicateStatus = UploaderAPI.requestFileDuplicateCheck(originalName, fileHash)
            if (duplicateStatus) {
                logger.warn("Duplicate File >> {}/{}", file.filePath, file.fileName)
                return@tempFileDownload null
            }

            UploaderAPI.requestFileUpload(tempFile, originalName, file.fileSize, file.fileRating)

            MetaData(
                filePath = file.filePath,
                fileSource = file.fileSource,
                fileName = file.fileName,
                fileExtension = fileExtension,
                fileSize = file.fileSize,
                fileHash = fileHash,
                fileRating = file.fileRating,
                uploadDateTime = file.uploadDateTime
            )
        }.getOrNull()
    }

    /**
     * 메타 데이터 저장
     *
     * @param metaDataList 메타데이터 리스트
     * @param savePath 저장 경로 (파일명 포함)
     */
    private fun saveToMetadataFile(metaDataList: List<MetaData>, savePath: String?) {
        val headerList = listOf("저장경로", "파일출처", "파일명", "문서유형 (확장자)", "파일크기(Byte)", "파일해시", "파일등급", "문서업로드 일시")
        CsvUtil.saveCsvToFile(headerList, metaDataList, savePath)
    }
}