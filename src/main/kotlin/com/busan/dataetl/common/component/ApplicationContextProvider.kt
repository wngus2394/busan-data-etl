package com.busan.dataetl.common.component

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration

/**
 * Application Context 제공 설정 파일
 * (Object 파일에서 의존성 주입을 사용하지 않고 Bean 객체에 접근할 수 있도록 context를 제공하는 설정파일)
 *
 * @author 김재영
 * @since 0.1
 */
@Configuration
class ApplicationContextProvider : ApplicationContextAware {

    companion object {
        var context: ApplicationContext? = null
            private set
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}

/**
 * 빈 객체 접근 함수
 * @since 0.1
 */
inline fun <reified T : Any> getBean(): T? = ApplicationContextProvider.context?.getBean(T::class.java)
inline fun <reified T : Any> getBean(beanName: String): T = ApplicationContextProvider.context?.getBean(beanName) as T

fun getProp(key: String): String? = ApplicationContextProvider.context?.environment?.getProperty(key)!!
fun getProp(key: String, default: String): String =
    ApplicationContextProvider.context?.environment?.getProperty(key, default)!!