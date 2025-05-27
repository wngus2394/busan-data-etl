package com.busan.dataetl.common.type

/**
 * 문서 보안 레벨
 */
enum class SecurityLevel(val level: Int) {
    LOW(1),
    MEDIUM_LOW(2),
    MEDIUM(3),
    MEDIUM_HIGH(4),
    HIGH(5)
    ;

    companion object {

        /**
         * Code to SecurityLevel
         */
        fun convert(code: String): SecurityLevel = when (code) {
            "1.0등급" -> LOW
            "2.0등급" -> MEDIUM_LOW
            "3.0등급" -> MEDIUM
            "4.0등급" -> MEDIUM_HIGH
            else -> HIGH
        }
    }
}
