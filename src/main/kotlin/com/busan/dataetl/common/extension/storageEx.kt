package com.busan.dataetl.common.extension

/**
 * Object Storage Key 루트 폴더 추출
 */
fun String.extractRootFolder(): String =
    this.substringBefore('/', missingDelimiterValue = "")

/**
 * Object Storage Key 경로 추출
 */
fun String.extractPath(): String =
    if (this.contains('/')) this.substringBeforeLast("/") else ""

/**
 * Object Storage Key 파일명 추출
 */
fun String.extractFileName(): String =
    this.substringAfterLast("/")

/**
 * 파일 사이즈 변환 [Long] -> [Int]
 */
fun Long.safeToInt(): Int =
    if (this <= Int.MAX_VALUE) this.toInt()
    else throw IllegalArgumentException("파일 크기가 Int 범위를 초과했습니다.")


