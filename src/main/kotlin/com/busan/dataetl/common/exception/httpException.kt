package com.suteuk.orderplus.common.exception

/**
 * 400 Exception
 */
data class BadRequestException(
    override val message: String? = "잘못된 파라미터 입니다."
) : IllegalArgumentException(message)

/**
 * 500 Exception
 */
data class ServerException(
    override val message: String? = "서버 내부 오류입니다."
) : RuntimeException(message)