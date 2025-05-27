package com.busan.dataetl.common.dto.outside.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.suteuk.orderplus.common.exception.ServerException

data class CommonResponse(
    @JsonProperty("success")
    val successStatus: Boolean,

    @JsonProperty("data")
    val resultCode: String,

    @JsonProperty("message")
    val resultMessage: String
) {

    /**
     * 성공여부 검토
     */
    fun ensureSuccess() {
        if (!successStatus) {
            throw ServerException("($resultCode) $resultMessage")
        }
    }
}
