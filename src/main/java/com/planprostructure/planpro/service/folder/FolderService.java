package com.planprostructure.planpro.service.folder;

import java.util.List;

public interface FolderService {
    void createFolder(String folderName) throws Throwable;

    void updateFolder(Long folderId, String folderName) throws Throwable;

    void deleteFolder(Long folderId) throws Throwable;

    void addFileToFolder(Long folderId, List<Long> fileId) throws Throwable;
}
