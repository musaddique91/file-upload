package com.hrblizz.fileapi

import com.hrblizz.fileapi.dto.FileMetaDTO
import com.hrblizz.fileapi.dto.FilesMetaRequest
import com.hrblizz.fileapi.dto.UploadedFileAttribute
import com.hrblizz.fileapi.service.FileUploadService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(
    locations = ["classpath:application-test.properties"]
)
@RunWith(SpringRunner::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FileUploadTest {
    @Autowired
    lateinit var fileUploadService: FileUploadService
    var token: String? = null

    @Test
    @Order(1)
    fun `test file upload`() {
        val response = fileUploadService.save(getFileUploadParam())
        Assertions.assertNotNull(response)
        Assertions.assertNotNull(response.token)
        token = response.token
    }

    @Test
    @Order(2)
    fun `test getFile`() {
        val file = fileUploadService.getFile(token!!)
        Assertions.assertNotNull(file)
        Assertions.assertEquals("name1", file.filename)
    }

    @Test
    @Order(3)
    fun `test file Metas`() {
        val filesMetas = fileUploadService.getFiles(FilesMetaRequest(listOf(token!!)))
        Assertions.assertNotNull(filesMetas)
        Assertions.assertEquals(1, filesMetas.files.size)
    }

    @Test
    @Order(4)
    fun `delete file`() {
        val deleted = fileUploadService.delete(token!!)
        Assertions.assertEquals(true, deleted)
    }

    private fun getFileUploadParam(): UploadedFileAttribute {
        val mockMultipartFile =
            MockMultipartFile("name1", "file-name-1", "plain/text", "this is test file".toByteArray())
        return UploadedFileAttribute(
            "name1",
            "plain/text",
            FileMetaDTO(1),
            "timesheet",
            null,
            mockMultipartFile
        )
    }
}
