package com.busan.dataetl.common.extension

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.ResponseEntity

// 전역 ObjectMapper 싱글턴
private val jsonMapper = jacksonObjectMapper()

/**
 * Http 통신 응답 값 추출
 */
internal inline fun <reified T> ResponseEntity<*>.getObjectBody(): T =
    jsonMapper.readValue(requireNotNull(this.body) { "Response body is null" } as String)