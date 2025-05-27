package com.busan.dataetl.common.exception.handler

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest
import java.text.SimpleDateFormat

/**
 * 기본 에러 속성 설정
 * (시간 포맷 변경)
 */
@Component
class DefaultErrorHandler : DefaultErrorAttributes() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 날짜 포맷

    /**
     * 에러 속성 조회
     *
     * @param webRequest Request 정보
     * @param options 에러속성옵션
     * @return 에러반환 객체
     */
    override fun getErrorAttributes(webRequest: WebRequest?, options: ErrorAttributeOptions?): MutableMap<String, Any> {
        val errorAttributes = super.getErrorAttributes(webRequest, options)
        val timestamp = errorAttributes["timestamp"]
        errorAttributes["timestamp"] = dateFormat.format(timestamp)
        return errorAttributes
    }
}