package com.busan.dataetl.common.interceptor

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.io.IOException
import kotlin.math.abs
import kotlin.random.Random

/**
 * RestTemplate 로깅 처리
 */
class LoggingInterceptor : ClientHttpRequestInterceptor {

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    @Throws(IOException::class)
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        val session = createNumber()
        preHandle(session, request, body)
        val response = execution.execute(request, body)
        postHandle(session, response)

        return response
    }

    /**
     * 임시 Session 번호 생성
     *
     * @return 임시번호
     */
    private fun createNumber(): String {
        return abs(Random.nextInt() * 1000000).toString()
    }

    /**
     * API 통신 요청 로깅 처리
     * @param num Session 번호
     * @param request 통신 요청 데이터
     * @param body 요청 body 데이터
     */
    private fun preHandle(num: String, request: HttpRequest, body: ByteArray) {
        val bodyLog = request.headers.contentType
            ?.toString()
            ?.takeIf { it.contains("multipart/form-data", ignoreCase = true) }
            ?.let { "[multipart/form-data] 생략" }
            ?: String(body, Charsets.UTF_8)

        logger.info(
            "[{}][{}][{}] Header: {}, Body: {}",
            num,
            request.uri,
            request.method,
            request.headers,
            bodyLog
        )
    }

    /**
     * API 통신 응답 로깅 처리
     * @param num Session 번호
     * @param response 통신 응답 데이터
     */
    private fun postHandle(num: String, response: ClientHttpResponse) {
        val body = response.body.bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        logger.info("[{}][{}] Body: {}", num, response.statusCode, body)
    }
}