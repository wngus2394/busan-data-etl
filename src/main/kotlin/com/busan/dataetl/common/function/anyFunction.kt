package com.busan.dataetl.common.function

import org.apache.coyote.BadRequestException


/**
 * 파라미터 검증
 *
 * @param params 검증 파라미터
 */
fun validateParams(vararg params: Any) {
    if (params.any { (it is Int && it <= 0) || (it is String && it.isEmpty()) || (it is List<*> && it.isEmpty()) }) {
        throw BadRequestException()
    }
}