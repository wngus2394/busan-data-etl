package com.busan.dataetl.util

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import com.busan.dataetl.common.component.getProp
import com.busan.dataetl.common.dto.S3Object
import com.busan.dataetl.common.extension.extractFileName
import com.busan.dataetl.common.extension.extractPath
import com.busan.dataetl.common.extension.extractRootFolder
import com.busan.dataetl.common.extension.safeToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File

/**
 * Storage 처리 함수 모음
 * S3 또는 NCP Object Storage 연결 및 처리 기능들의 함수들이 모여있는 Object 파일이다.
 */
object StorageUtil {
    private val ncpAccessKey: String by lazy { getProp("ncp.object-storage.accessKey")!! }
    private val ncpSecretKey: String by lazy { getProp("ncp.object-storage.secretKey")!! }
    private val ncpEndpoint: String by lazy { getProp("ncp.object-storage.endpoint")!! }
    private val ncpRegion: String by lazy { getProp("ncp.object-storage.region")!! }

    private val s3Client: AmazonS3 by lazy {
        val credentials = BasicAWSCredentials(ncpAccessKey, ncpSecretKey)
        val endpoint = AwsClientBuilder.EndpointConfiguration(ncpEndpoint, ncpRegion)

        AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(endpoint)
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build()
    }

    /**
     * 파일 메타데이터 조회
     *
     * @param bucketName 버킷명
     * @param path 저장할 경로 ex) test
     * @param fileName 파일명
     * @return 메타데이터
     */
    fun getObjectMetadata(bucketName: String, path: String, fileName: String): ObjectMetadata {
        val objectName = "$path/$fileName"
        return s3Client.getObjectMetadata(bucketName, objectName)
    }

    /**
     * Object 파일 리스트 조회
     *
     * @param bucketName 버킷명
     * @return 파일 정보 리스트
     * {@link common.dto#S3Object}
     */
    fun getObjectList(bucketName: String): List<S3Object> {
        val initialRequest = ListObjectsRequest()
            .withBucketName(bucketName)
            .withMaxKeys(300)

        return buildList {
            var objectListing = s3Client.listObjects(initialRequest)

            while (true) {
                objectListing.objectSummaries.mapTo(this) { summary ->
                    val fullKey = summary.key

                    val rootFolder = fullKey.extractRootFolder()
                    val path = fullKey.extractPath()
                    val fileName = fullKey.extractFileName()
                    val sizeAsInt = summary.size.safeToInt()

                    S3Object(
                        bucketName = bucketName,
                        rootFolder = rootFolder,
                        filePath = path,
                        fileName = fileName,
                        fileSize = sizeAsInt
                    )
                }

                /*
                 withMaxKeys 지정된 개수 이상으로 존재할 경우 isTruncated() 값이 Ture
                 False일 경우 더이상 값이 없다는 의미
                 */
                if (objectListing.isTruncated) {
                    objectListing = s3Client.listNextBatchOfObjects(objectListing)
                } else {
                    break
                }
            }
        }
    }

    /**
     * 파일 업로드
     *
     * @param file 업로드할 파일 객체
     * @param bucketName 버킷명
     * @param path 저장할 경로 ex) test/
     * @param fileName 파일명 (지정하지 않을 경우 [file]의 이름으로 적용된다.)
     * @return 이미지 URL 주소
     */
    fun fileUpload(file: File, bucketName: String, path: String, fileName: String = file.name): String {
        val objectName = "$path$fileName"
        val putObjectRequest = PutObjectRequest(bucketName, objectName, file)
            .withCannedAcl(CannedAccessControlList.PublicRead)
        val result = s3Client.putObject(putObjectRequest)

        if (result.eTag.isNullOrEmpty()) {
            throw IllegalStateException("파일 업로드 과정에서 문제가 발생하였습니다.")
        }

        return s3Client.getUrl(bucketName, objectName).toString()
            .substringBeforeLast("/")
    }

    /**
     * 파일 업로드
     *
     * @param fileBytes 업로드할 파일 데이터 [ByteArray]
     * @param fileContentType 업로드할 파일 ContentType
     * @param bucketName 버킷명
     * @param path 저장할 경로 ex) test/
     * @param fileName 파일명
     * @return 이미지 URL 주소
     */
    fun fileUpload(
        fileBytes: ByteArray,
        fileContentType: String?,
        bucketName: String,
        path: String,
        fileName: String
    ): String {
        val byteArrayInputStream = ByteArrayInputStream(fileBytes)
        val objectName = "$path$fileName"
        val metadata = ObjectMetadata().apply {
            contentLength = fileBytes.size.toLong()
            contentType = fileContentType ?: "image/jpeg"
        }

        val putObjectRequest = PutObjectRequest(bucketName, objectName, byteArrayInputStream, metadata)
            .withCannedAcl(CannedAccessControlList.PublicRead)
        val result = s3Client.putObject(putObjectRequest)

        if (result.eTag.isNullOrEmpty()) {
            throw IllegalStateException("파일 업로드 과정에서 문제가 발생하였습니다.")
        }

        return s3Client.getUrl(bucketName, objectName).toString()
            .substringBeforeLast("/")
    }

    /**
     * 파일 업로드 (비동기)
     *
     * @param fileBytes 업로드할 파일 데이터 [ByteArray]
     * @param fileContentType 업로드할 파일 ContentType
     * @param bucketName 버킷명
     * @param path 저장할 경로 ex) test/
     * @param fileName 파일명
     * @return 이미지 URL 주소
     */
    suspend fun asyncFileUpload(
        fileBytes: ByteArray,
        fileContentType: String?,
        bucketName: String,
        path: String,
        fileName: String
    ): String {
        return withContext(Dispatchers.IO) {
            fileUpload(fileBytes, fileContentType, bucketName, path, fileName)
        }
    }

    /**
     * 임시 파일 다운로드
     *
     * @param bucketName 버킷명
     * @param path 저장할 경로 ex) test
     * @param fileName 파일명
     * @param op 추가 로직
     */
    fun tempFileDownload(bucketName: String, path: String, fileName: String, op: (String, File) -> Unit) {
        val objectName = "$path/$fileName"
        val safeFileName = encodeSafeFileName(fileName)
        val s3Object = GetObjectRequest(bucketName, objectName)

        var outputFile: File? = null

        try {
            val s3InputStream = s3Client.getObject(s3Object).objectContent
            val tempDir = File(System.getProperty("java.io.tmpdir"))

            outputFile = File(tempDir, safeFileName).apply {
                createNewFile()
            }

            outputFile.outputStream().use { output ->
                s3InputStream.use { input ->
                    input.copyTo(output)
                }
            }

            op(safeFileName, outputFile)
        } catch (e: Exception) {
            throw e
        } finally {
            outputFile?.takeIf { it.exists() }?.delete()
        }
    }

    /**
     * 임시 파일 다운로드 (비동기)
     *
     * @param bucketName 버킷명
     * @param path 저장할 경로 ex) test/
     * @param fileName 파일명
     * @param op 추가 로직
     */
    suspend fun asyncTempFileDownload(bucketName: String, path: String, fileName: String, op: (String, File) -> Unit) {
        withContext(Dispatchers.IO) {
            tempFileDownload(bucketName, path, fileName, op)
        }
    }

    /**
     * 파일 삭제
     *
     * @param bucketName 버킷명
     * @param objectName 저장할 경로 + 파일명
     */
    fun removeFile(bucketName: String, objectName: String) {
        s3Client.deleteObject(bucketName, objectName)
    }

    /**
     * 파일명 특수문자 인코딩
     *
     * @param fileName 파일명
     * @return 인코딩된 파일명
     */
    private fun encodeSafeFileName(fileName: String): String {
        val specialChars = mapOf(
            '+' to "%2B", '#' to "%23", '&' to "%26",
            '=' to "%3D", '?' to "%3F", '/' to "%2F", '%' to "%25"
        )

        return fileName.map { specialChars[it] ?: it }.joinToString("")
    }
}