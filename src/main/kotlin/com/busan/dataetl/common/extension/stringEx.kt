package com.busan.dataetl.common.extension

private val csvSpecialChars = Regex("[,\n\r\"]")

/**
 * CSV용 문자열 처리
 */
fun String.csvEscape(): String {
    return if (csvSpecialChars.containsMatchIn(this)) {
        "\"${this.replace("\"", "\"\"")}\""
    } else this
}