package com.hrblizz.fileapi.service.impl

import com.hrblizz.fileapi.config.AppConfig
import com.hrblizz.fileapi.controller.FileController
import com.hrblizz.fileapi.controller.exception.FileApiException
import com.hrblizz.fileapi.data.entities.FileUploadEntity
import com.hrblizz.fileapi.data.repository.FileUploadEntityRepository
import com.hrblizz.fileapi.dto.FileInfoDTO
import com.hrblizz.fileapi.dto.FileUploadDTO
import com.hrblizz.fileapi.dto.FilesDTO
import com.hrblizz.fileapi.dto.FilesMetaRequest
import com.hrblizz.fileapi.dto.ResourceDTO
import com.hrblizz.fileapi.dto.UploadedFileAttribute
import com.hrblizz.fileapi.rest.Errors
import com.hrblizz.fileapi.service.FileUploadService
import org.springframework.core.io.UrlResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.annotation.PostConstruct

@Service
class FileUploadServiceImpl(
    private val appConfig: AppConfig,
    private val fileUploadRepository: FileUploadEntityRepository
) : FileUploadService {
    var path: Path = Paths.get(appConfig.fileBaseStoragePath)

    @PostConstruct
    fun initialize() {
        initializeStorage()
    }

    override fun initializeStorage() {
        try {
            if (!Files.exists(path)) {
                val mkdir = File(appConfig.fileBaseStoragePath).mkdir()
                if (!mkdir) {
                    throw FileApiException(Errors.PATH_DOES_NOT_EXIST)
                }
            }
        } catch (e: IOException) {
            throw FileApiException(e.message ?: Errors.ROOT_PATH_ERROR)
        }
    }

    override fun save(
        fileAttributes: UploadedFileAttribute
    ): FileUploadDTO {
        return try {
            val fileName = fileAttributes.file.originalFilename ?: fileAttributes.name
            val filePath = path.resolve(fileName)
            Files.copy(fileAttributes.file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
            val savedFileEntity = fileUploadRepository.save(
                FileUploadEntity(
                    name = fileAttributes.name,
                    originalUploadedFileName = fileAttributes.file.originalFilename,
                    contentType = fileAttributes.contentType,
                    meta = fileAttributes.meta,
                    source = fileAttributes.source,
                    expireTime = fileAttributes.expireTime,
                    storagePath = filePath.toAbsolutePath().toString()
                )
            )
            val fileUploadDTO = FileUploadDTO(savedFileEntity.id)
            fileUploadDTO.add(linkTo<FileController> { getFile(savedFileEntity.id) }.withRel("GET"))
            fileUploadDTO.add(linkTo<FileController> { deleteFile(savedFileEntity.id) }.withRel("DELETE"))
        } catch (e: Exception) {
            throw FileApiException(e.message ?: "Error Occurred while file saving")
        }
    }

    override fun getFile(token: String): ResourceDTO {
        val fileData = fileUploadRepository.findByIdOrNull(token)
        if (fileData == null) {
            throw FileApiException(Errors.NO_RECORD_FOUND)
        } else {
            val path = Paths.get(fileData.storagePath)
            val resource = UrlResource(path.toUri())
            return if (resource.exists() && resource.isReadable) {
                ResourceDTO(
                    resource,
                    fileData.name,
                    resource.contentLength(),
                    fileData.contentType,
                    fileData.createdDate
                )
            } else throw FileApiException(Errors.FILE_NOT_READABLE)
        }
    }

    override fun delete(token: String): Boolean {
        val fileData = fileUploadRepository.findByIdOrNull(token)
        if (fileData == null) {
            throw FileApiException(Errors.NO_RECORD_FOUND)
        } else {
            return if (Files.deleteIfExists(Paths.get(fileData.storagePath))) {
                fileUploadRepository.deleteById(token)
                true
            } else {
                false
            }
        }
    }

    override fun getFiles(request: FilesMetaRequest): FilesDTO {
        val entities = fileUploadRepository.findAllById(request.tokens)
        val list = mutableListOf<FileInfoDTO>()
        entities.forEach {
            val fileInfo = FileInfoDTO(
                it.id, it.name, File(it.storagePath).length(), it.contentType, it.createdDate, it.meta
            )
            fileInfo.add(linkTo<FileController> { getFile(fileInfo.token) }.withRel("GET"))
            fileInfo.add(linkTo<FileController> { deleteFile(fileInfo.token) }.withRel("DELETE"))
            list.add(
                fileInfo
            )
        }
        return FilesDTO(list)
    }
}
