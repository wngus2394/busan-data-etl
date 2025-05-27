package com.busan.dataetl.api

import com.busan.dataetl.common.abstract.ApiLoader
import com.busan.dataetl.common.dto.outside.response.CommonResponse
import com.busan.dataetl.common.extension.getObjectBody
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * 파일 업로드 API 통신 영역
 */
object UploaderAPI : ApiLoader("rest.api.file-uploader.path") {

    /**
     * 파일 중복 체크
     *
     * @param fileName 파일명
     * @param fileHash 파일해쉬 (SHA-256)
     * @return 파일 중복 여부 (true: 중복 or 오류)
     */
    fun requestFileDuplicateCheck(fileName: String, fileHash: String): Boolean {
        val url = "$baseUrl/api/preprocess/upload/duplicationcheck"
        val builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("fileName", fileName)
            .queryParam("fileHash", fileHash)
            .build()
            .toUriString()

        val entity = HttpEntity<Void>(header)
        val response = restClient.exchange<String>(builder, HttpMethod.POST, entity)
            .getObjectBody<CommonResponse>()

        // 응답값 false일 경우 중복 파일이기 때문에 true 값으로 변경해서 반환
        return !response.successStatus
    }

    /**
     * 파일 업로드
     *
     * @param file 업로드 파일
     * @param fileName 파일명
     * @param fileSize 파일크기 (byte)
     * @param securityLevel 보안등급
     */
    fun requestFileUpload(file: File, fileName: String, fileSize: Int, securityLevel: Int) {
        val url = "$baseUrl/api/preprocess/upload/file"
        val builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("fileName", fileName)
            .queryParam("fileSize", fileSize)
            .queryParam("uploadSub", "non_auto")
            .queryParam("auth", securityLevel)
            .build()
            .toUriString()

        val multipartHeaders = HttpHeaders().apply {
            contentType = MediaType.MULTIPART_FORM_DATA
        }

        val body = LinkedMultiValueMap<String, Any>().apply {
            add("file", FileSystemResource(file))
        }

        restClient.exchange<String>(builder, HttpMethod.POST, HttpEntity(body, multipartHeaders))
            .getObjectBody<CommonResponse>()
            .ensureSuccess()
    }
}