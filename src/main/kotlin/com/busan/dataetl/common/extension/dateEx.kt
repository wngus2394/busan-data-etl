package com.busan.dataetl.common.extension

import java.text.SimpleDateFormat
import java.util.*

/**
 * [Date] to [String]
 */
fun Date.toDateTimeFormat(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(this)
}