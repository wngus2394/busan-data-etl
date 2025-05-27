package com.busan.dataetl.common.exception.handler

import com.busan.dataetl.common.dto.Error
import com.suteuk.orderplus.common.exception.BadRequestException
import com.suteuk.orderplus.common.exception.ServerException
import org.slf4j.LoggerFactory
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import java.sql.SQLException

/**
 * 전역 에러 핸들러
 */
@RestControllerAdvice
class ExceptionHandler(
    private val defaultErrorHandler: DefaultErrorHandler
) {

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    /**
     * 400 에러
     */
    @ExceptionHandler(BadRequestException::class, MethodArgumentNotValidException::class)
    fun badRequest(request: WebRequest, ex: Throwable): ResponseEntity<*> {
        return exceptionResolver(HttpStatus.BAD_REQUEST, request, ex)
    }

    /**
     * 500 에러
     */
    @ExceptionHandler(
        ServerException::class,
        SQLException::class,
        IllegalStateException::class,
        Exception::class
    )
    fun internalServer(request: WebRequest, ex: Throwable): ResponseEntity<*> {
        return exceptionResolver(HttpStatus.INTERNAL_SERVER_ERROR, request, ex)
    }

    /**
     * 에러 데이터 객체 변환
     */
    private fun exceptionResolver(statusCode: HttpStatus, request: WebRequest, ex: Throwable): ResponseEntity<*> {
        val attributes = defaultErrorHandler.getErrorAttributes(request, ErrorAttributeOptions.defaults())
        val timestamp = attributes["timestamp"].toString()
        val path = (request as? ServletWebRequest)?.request?.requestURI ?: "unknown"
        val message: String = if (ex is SQLException) {
            "You have an error in your SQL syntax"
        } else {
            ex.message ?: "An unknown error occurred"
        }

        logger.error("[{}] : {}", statusCode.value(), ex.stackTraceToString())
        return ResponseEntity(
            Error(timestamp = timestamp, error = statusCode.reasonPhrase, path = path, message),
            statusCode
        )
    }
}