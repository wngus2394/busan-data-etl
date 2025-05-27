package com.busan.dataetl.common.extension

/**
 * [Boolean] to [Int]
 */
fun Boolean.toInt(): Int = if (this) 1 else 0