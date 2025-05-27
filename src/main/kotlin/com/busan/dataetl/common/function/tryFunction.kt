package com.busan.dataetl.common.function

import kotlinx.coroutines.*
import org.slf4j.Logger

/**
 * 비동기로직 예외처리 실행
 */
inline fun runAsyncTry(
    methodName: String,
    logger: Logger,
    crossinline block: suspend CoroutineScope.() -> Unit,
): Job {
    val handler = CoroutineExceptionHandler { _, ex ->
        logger.error("[RUNASYNC][{}] {}", methodName, ex.stackTraceToString())
    }
    return CoroutineScope(Dispatchers.Default).launch(handler) { block(this) }
}