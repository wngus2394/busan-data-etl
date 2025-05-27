package com.busan.dataetl.service.file

import com.busan.dataetl.common.dto.PageableResponse
import com.busan.dataetl.common.dto.file.request.AddManualUploadFileRequest
import com.busan.dataetl.common.dto.file.request.AddUploadFileRequest
import com.busan.dataetl.common.dto.file.request.GetFileRequest
import com.busan.dataetl.common.dto.file.response.GetFileResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/files")
class FileController(
    private val fileService: FileService
) {

    /**
     * 파일 리스트 조회
     * Object Storage에 등록되어 있는 전체 파일 리스트를 조회합니다.
     */
    @GetMapping
    fun findAllFileList(request: GetFileRequest): PageableResponse<List<GetFileResponse>> {
        return fileService.findFileList(request)
    }

    /**
     * 파일 업로드
     * Object Storage에 등록되어 있는 파일 리스트를 조회 후 Embedding Was로 요청합니다.
     */
    @PostMapping
    fun addUploadFile(@RequestBody request: AddUploadFileRequest) {
        fileService.addUploadFile(request)
    }

    /**
     * 파일 업로드 (수동)
     * 파일 경로와 파일명을 사용하여 특정 파일들을 직접 Embedding Was로 요청합니다.
     */
    @PostMapping("/manual")
    fun addManualUploadFile(@RequestBody request: AddManualUploadFileRequest) {
        fileService.addManualUploadFile(request)
    }
}