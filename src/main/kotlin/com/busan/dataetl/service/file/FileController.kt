package com.busan.dataetl.service.file

import com.busan.dataetl.common.dto.PageableResponse
import com.busan.dataetl.common.dto.file.request.AddManualUploadFileRequest
import com.busan.dataetl.common.dto.file.request.AddUploadFileRequest
import com.busan.dataetl.common.dto.file.request.GetFileRequest
import com.busan.dataetl.common.dto.file.response.GetFileResponse
import com.busan.dataetl.common.openapi.response.ApiErrorResponse
import com.busan.dataetl.common.openapi.response.ApiSuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "파일 처리", description = "파일과 관련된 API 기능을 제공합니다.")
@ApiErrorResponse
@RestController
@RequestMapping("/files")
class FileController(
    private val fileService: FileService
) {

    @Operation(summary = "파일 리스트 조회", description = "Object Storage에 업로드 되어 있는 전체 파일 리스트를 조회합니다.")
    @ApiSuccessResponse(
        content = [Content(array = ArraySchema(schema = Schema(implementation = GetFileResponse::class)))]
    )
    @GetMapping
    fun findAllFileList(request: GetFileRequest): PageableResponse<List<GetFileResponse>> {
        return fileService.findFileList(request)
    }

    @Operation(summary = "파일 업로드", description = "Object Storage에 등록되어 있는 파일 조회·다운로드 후 Embedding Was로 요청합니다.")
    @ApiSuccessResponse
    @PostMapping
    fun addUploadFile(@RequestBody request: AddUploadFileRequest) {
        fileService.addUploadFile(request)
    }

    @Operation(summary = "파일 업로드 (수동)", description = "지정된 경로 및 파일명을 사용하여 조회·다운로드 후 Embedding Was로 요청합니다.")
    @ApiSuccessResponse
    @PostMapping("/manual")
    fun addManualUploadFile(@RequestBody request: AddManualUploadFileRequest) {
        fileService.addManualUploadFile(request)
    }
}