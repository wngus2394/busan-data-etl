package com.busan.dataetl.common.dto

data class Error(
    val timestamp: String, // 발생시간
    val error: String, // 오류항목
    val path: String, // 발생경로
    val message: String // 오류메세지
)