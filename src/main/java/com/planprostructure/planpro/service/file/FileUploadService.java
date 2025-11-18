package com.planprostructure.planpro.service.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    Object uploadImage(MultipartFile fileData) throws Exception;

}