package com.busan.dataetl.common.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * Log 처리 모듈화 (AOP)
 */
@Aspect
@Component
class LogAspect {

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    @Pointcut("execution(* com.busan.dataetl.service.*.*.*(..)) && !execution(* com.busan.dataetl.service.home.*.*(..))")
    fun applicationServiceMethods() {
    }

    @Around("applicationServiceMethods()")
    @Throws(Exception::class)
    fun applicationAround(joinPoint: ProceedingJoinPoint): Any? {
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name
        val ip = extractClientIp()
        val parameter = extractMethodParameters(joinPoint)
        logger.info("[{}/{}][{}][START] {}", className, methodName, ip, parameter.toString())
        val result = joinPoint.proceed()
        logger.info("[{}/{}][{}][END] {}", className, methodName, ip, result?.toString() ?: "")
        return result
    }

    /**
     * Client IP 주소 추출
     */
    private fun extractClientIp(): String {
        val requestContext = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        val request = requestContext?.request

        return request?.getHeader("X-Forwarded-For")?.split(",")?.firstOrNull()?.trim()
            ?: request?.remoteAddr ?: "Unknown IP"
    }

    /**
     * Parameter 데이터 추출
     */
    private fun extractMethodParameters(joinPoint: ProceedingJoinPoint): Map<String, Any?> {
        return (joinPoint.signature as? MethodSignature)?.parameterNames
            ?.zip(joinPoint.args)
            ?.toMap()
            ?: emptyMap()
    }
}