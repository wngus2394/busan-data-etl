package com.busan.dataetl.common.abstract

import com.busan.dataetl.common.component.getBean
import com.busan.dataetl.common.component.getProp
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

/**
 * 외부통신 API 공통영역
 */
abstract class ApiLoader(private val urlPropKey: String) {
    protected val restClient = getBean<RestTemplate>()!!

    // 기본경로
    protected val baseUrl: String
        get() = getProp(urlPropKey)!!

    // 헤더정보
    protected open val header = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
    }

    init {
        require(baseUrl.isNotBlank()) { "API Base URL이 설정되지 않았습니다. ($urlPropKey)" }
    }
}