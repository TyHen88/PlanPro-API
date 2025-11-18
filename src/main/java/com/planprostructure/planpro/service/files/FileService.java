package com.planprostructure.planpro.service.files;

import java.awt.print.Pageable;

public interface FileService {
    Object getFiles(Pageable pageable) throws Throwable;

//    void uploadFile(String fileName, String fileType, String filePath) throws Throwable;
}
