package com.busan.dataetl.service.file

import com.busan.dataetl.api.UploaderAPI
import com.busan.dataetl.common.dto.PageableResponse
import com.busan.dataetl.common.dto.S3Object
import com.busan.dataetl.common.dto.file.request.AddManualUploadFileRequest
import com.busan.dataetl.common.dto.file.request.AddUploadFileRequest
import com.busan.dataetl.common.dto.file.request.GetFileRequest
import com.busan.dataetl.common.dto.file.response.GetFileResponse
import com.busan.dataetl.common.extension.extractRootFolder
import com.busan.dataetl.common.extension.safeToInt
import com.busan.dataetl.common.function.validateParams
import com.busan.dataetl.common.type.SecurityLevel
import com.busan.dataetl.util.FileUtil
import com.busan.dataetl.util.StorageUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

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
                rootFolder = it.rootFolder,
                filePath = it.filePath,
                fileName = it.fileName,
                fileSize = it.fileSize
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
        var failCount = 0

        fileList.forEach { file ->
            try {
                processFile(bucketName, file)
            } catch (e: Exception) {
                logger.error("File processing failed >> {}/{} - {}", file.filePath, file.fileName, e.localizedMessage)
                failCount++
            }
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

        fileList.forEach { data ->
            try {
                val metadata = StorageUtil.getObjectMetadata(bucketName, data.storageFullPath, data.fileName)
                val rootFolder = data.storageFullPath.extractRootFolder()
                val fileSize = metadata.contentLength.safeToInt()

                val fileObject = S3Object(
                    bucketName = bucketName,
                    rootFolder = rootFolder,
                    filePath = data.storageFullPath,
                    fileName = data.fileName,
                    fileSize = fileSize
                )

                processFile(bucketName, fileObject)
            } catch (e: Exception) {
                logger.error("File processing failed >> {} - {}", data.fileName, e.localizedMessage)
                throw IllegalStateException("파일 처리 실패")
            }
        }
    }

    /**
     * 파일 수집/저장 처리
     *
     * @param bucketName 버킷명
     * @param file 파일 정보 객체
     */
    private fun processFile(bucketName: String, file: S3Object) {
        StorageUtil.tempFileDownload(bucketName, file.filePath, file.fileName) { safeFileName, tempFile ->
            val fileHash = FileUtil.calculateSHA256(tempFile.toPath())
            val duplicateStatus = UploaderAPI.requestFileDuplicateCheck(safeFileName, fileHash)

            if (duplicateStatus) {
                logger.warn("Duplicate File >> {}/{}", file.filePath, file.fileName)
                return@tempFileDownload
            }

            val securityCode = file.rootFolder.substringAfterLast('_')
            val securityLevel = SecurityLevel.convert(securityCode)

            UploaderAPI.requestFileUpload(tempFile, safeFileName, file.fileSize, securityLevel.level)
        }
    }
}