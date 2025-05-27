package com.busan.dataetl.common.function

/**
 * Method 이름 조회
 */
fun getMethodName(): String? {
    return StackWalker.getInstance().walk { frames ->
        frames.skip(1).findFirst().map { it.methodName }.orElse(null)
    }
}