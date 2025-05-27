package com.busan.dataetl.util

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

/**
 * File 처리 함수 모음
 */
object FileUtil {

    /**
     * 파일 해시값 계산 (SHA-256)
     *
     * @param filePath 대상 파일 경로
     * @param bufferSize 청크당 읽을 바이트 수
     * @return 파일 해시값
     */
    fun calculateSHA256(filePath: Path, bufferSize: Int = DEFAULT_BUFFER_SIZE): String {
        val digest = MessageDigest.getInstance("SHA-256")

        Files.newInputStream(filePath).use { input ->
            val buffer = ByteArray(bufferSize)
            var bytesRead: Int

            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }

        // 결과 바이트 배열을 16진수 문자열로 변환
        return digest.digest()
            .joinToString(separator = "") { "%02x".format(it) }
    }
}