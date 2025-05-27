package com.busan.dataetl

import com.busan.dataetl.util.StorageUtil
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.io.File
import java.io.FileReader
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

@ActiveProfiles("dev")
@SpringBootTest
class DataEtlWasApplicationTests {

    private val ROOT_DIRECTORY = "C:\\Users\\USER\\OneDrive\\바탕 화면\\Claion\\"

    @Test
    fun contextLoads() {
        val pathAndFileName = "데이터 품질\\(원본)데이터_품질_검사.csv"
        val csvFile = File(ROOT_DIRECTORY, pathAndFileName)

        val parser = CSVParserBuilder().withSeparator(',') // 쉼표 구분자
            .withEscapeChar('\u0000') // 👈 이스케이프 문자 제거
            .withQuoteChar('"') // 인용부호
            .build()

        val reader = CSVReaderBuilder(FileReader(csvFile, StandardCharsets.UTF_8))
            .withCSVParser(parser)
            .build()

        val failFileList = mutableListOf<String>()
        var successCount = 0
        var failCount = 0

        reader.use { reader ->
            val header = reader.readNext() ?: return
            header[0] = header[0].replace("\uFEFF", "") // BOM 제거

            val fileTypeIndex = header.indexOf("Type")
            val mainPathIndex = header.indexOf("Root")
            val pathIndex = header.indexOf("Path")
            val fileNameIndex = header.indexOf("Name")
            val scoreIndex = header.indexOf("Score")

            val uploadBase = File(ROOT_DIRECTORY, "2 부산시 학습자료")

            var row = reader.readNext()
            while (row != null) {
                val fileType = row.getOrNull(fileTypeIndex)?.trim()
                val mainPath = row.getOrNull(mainPathIndex)?.trim()
                val path = row.getOrNull(pathIndex)?.trim()
                val fileName = row.getOrNull(fileNameIndex)?.trim()
                val score = row.getOrNull(scoreIndex)?.trim()

                if (fileType == "File" && !fileName.isNullOrEmpty()) {
                    val fullPath = Paths.get(uploadBase.toString(), mainPath, path, fileName).toFile()
                    if (fullPath.exists()) {
                        val sanitizedPath = path?.replace(File.separator, "/")
                        val storagePath = "품질_${score}등급/$mainPath/$sanitizedPath/"

                        StorageUtil.fileUpload(
                            file = fullPath, bucketName = "busan-ai", path = storagePath, fileName = fileName
                        )

                        println("File upload success: $storagePath$fileName")
                        successCount++
                    } else {
                        println("File is not exist: $fullPath")
                        failFileList.add(fullPath.toString())
                        failCount++
                    }
                }

                row = reader.readNext()
            }
        }

        println("File upload complete. \n\t - Total Count (Success: $successCount / Fail: $failCount)")

        if (failCount > 0) {
            println("Fail File List >> ")
            failFileList.forEach {
                println(it)
            }
        }
    }
}