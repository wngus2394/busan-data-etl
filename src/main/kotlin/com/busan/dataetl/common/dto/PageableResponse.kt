package com.busan.dataetl.common.dto

data class PageableResponse<T>(
    val totalPageCount: Int?,
    val data: T
)
