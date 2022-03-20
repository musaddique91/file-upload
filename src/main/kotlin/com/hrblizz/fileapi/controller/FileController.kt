package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.constant.FileConstants
import com.hrblizz.fileapi.dto.FileUploadDTO
import com.hrblizz.fileapi.dto.FilesDTO
import com.hrblizz.fileapi.dto.FilesMetaRequest
import com.hrblizz.fileapi.dto.ResourceDTO
import com.hrblizz.fileapi.mapper.FileMapper
import com.hrblizz.fileapi.service.FileUploadService
import org.springframework.core.io.Resource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
class FileController(
    private val fileUploadService: FileUploadService,
    private val mapper: FileMapper
) {
    @PostMapping("files")
    fun uploadFile(
        @RequestParam("name") name: String,
        @RequestParam("contentType", required = false) contentType: String,
        @RequestParam("meta") meta: String,
        @RequestParam("source") source: String,
        @RequestParam(
            "expireTime",
            required = false
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) expireTime: LocalDateTime?,
        @RequestParam("content") file: MultipartFile
    ): ResponseEntity<FileUploadDTO> {
        val fileContentType = file.contentType ?: contentType
        val fileAttributes = mapper.mapBean(file, name, fileContentType, meta, source, expireTime)
        val fileUploadDTO = fileUploadService.save(fileAttributes)
        return ResponseEntity(fileUploadDTO, HttpStatus.CREATED)
    }

    @PostMapping("files/metas")
    fun getFilesMetas(@RequestBody request: FilesMetaRequest): ResponseEntity<FilesDTO> {
        return ok(fileUploadService.getFiles(request))
    }

    @GetMapping("file/{token}")
    fun getFile(@PathVariable token: String): ResponseEntity<Resource> {
        val resource = fileUploadService.getFile(token)
        return ok().headers(getHttpHeaders(resource))
            .body(resource.resource)
    }

    @DeleteMapping("file/{token}")
    fun deleteFile(@PathVariable token: String): ResponseEntity<Boolean> {
        return ok(fileUploadService.delete(token))
    }

    private fun getHttpHeaders(resource: ResourceDTO): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.add(FileConstants.HEADER_FILE_NAME, resource.filename)
        httpHeaders.add(FileConstants.HEADER_CONTENT_TYPE, resource.contentType)
        httpHeaders.add(FileConstants.HEADER_FILE_SIZE, resource.fileSize.toString())
        httpHeaders.add(FileConstants.HEADER_CREATED_TIME, resource.createTime.toString())
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment")
        return httpHeaders
    }
}
