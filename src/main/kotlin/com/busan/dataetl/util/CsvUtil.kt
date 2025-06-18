package com.busan.dataetl.util

import com.busan.dataetl.common.dto.file.MetaData
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * CSV 파일 생성 함수 모음
 */
object CsvUtil {

    /**
     * [MetaData] 리스트 CSV 형식 문자열로 변환
     *
     * @param headerList CSV 헤더 리스트
     * @param objectList [MetaData] 리스트
     * @return CSV 형식 문자열
     */
    fun generateCsvText(headerList: List<String>, objectList: List<MetaData>): String {
        return buildString {
            appendLine(listToCsvText(headerList))

            objectList.forEach { obj ->
                appendLine(obj.toCsvRow())
            }
        }
    }

    /**
     * CSV 파일 저장
     *
     * @param csvText CSV 저장 데이터
     * @param outputPath 저장 경로 (파일명 포함)
     */
    fun saveCsvToFile(csvText: String, outputPath: String?) {
        val finalPath = outputPath?.takeIf { it.isNotBlank() }
            ?: "output_${System.currentTimeMillis()}.csv"

        val file = File(finalPath)
        file.parentFile?.mkdirs()
        file.writeText(csvText, StandardCharsets.UTF_8)

        println("✅ CSV 저장 위치: ${file.absolutePath}")
    }

    /**
     * CSV 파일 저장 (대용량 처리 최적화)
     *
     * @param headerList CSV 헤더 리스트
     * @param objectList [MetaData] 리스트
     * @param outputPath 저장 경로 (파일명 포함)
     */
    fun saveCsvToFile(headerList: List<String>, objectList: List<MetaData>, outputPath: String?) {
        val finalPath = outputPath?.takeIf { it.isNotBlank() }
            ?: "output_${System.currentTimeMillis()}.csv"

        val file = File(finalPath)
        val header = listToCsvText(headerList)
        file.parentFile?.mkdirs()

        BufferedWriter(OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8)).use { writer ->
            writer.write(header)
            writer.newLine()

            objectList.forEach { obj ->
                writer.write(obj.toCsvRow())
                writer.newLine()
            }
        }

        println("✅ CSV 저장 위치: ${file.absolutePath}")
    }

    /**
     * [T] 리스트 CSV 형식 문자열로 변환
     *
     * @param dataList 데이터 리스트
     * @param separator 변환 구분자
     * @return CSV 형식 문자열
     */
    private fun <T> listToCsvText(dataList: List<T>, separator: String = ","): String {
        return dataList.joinToString(separator = separator)
    }
}