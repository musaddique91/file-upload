package com.hrblizz.fileapi.service

import com.hrblizz.fileapi.dto.FileUploadDTO
import com.hrblizz.fileapi.dto.FilesDTO
import com.hrblizz.fileapi.dto.FilesMetaRequest
import com.hrblizz.fileapi.dto.ResourceDTO
import com.hrblizz.fileapi.dto.UploadedFileAttribute

interface FileUploadService {
    fun initializeStorage()
    fun save(
        fileAttributes: UploadedFileAttribute
    ): FileUploadDTO

    fun getFile(token: String): ResourceDTO
    fun delete(token: String): Boolean
    fun getFiles(request: FilesMetaRequest): FilesDTO
}
