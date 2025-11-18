package com.planprostructure.planpro.service.folder;

import com.planprostructure.planpro.components.common.api.StatusCode;
import com.planprostructure.planpro.domain.files.FileRepository;
import com.planprostructure.planpro.domain.folder.Folder;
import com.planprostructure.planpro.domain.folder.FolderRepository;
import com.planprostructure.planpro.enums.Status;
import com.planprostructure.planpro.exception.BusinessException;
import com.planprostructure.planpro.helper.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;

    @Override
    @Transactional
    public void createFolder(String folderName) throws Throwable {
        var userId = AuthHelper.getUserId();
        var folder = folderRepository.findByName(folderName)
                .orElseGet(() -> Folder.builder()
                        .name(folderName)
                        .userId(userId)
                        .createdAt(java.time.LocalDateTime.now())
                        .status(com.planprostructure.planpro.enums.Status.NORMAL)
                        .build());
        if (folder.getId() == null) {
            folderRepository.save(folder);
        } else {
            throw new BusinessException(StatusCode.FOLDER_ALREADY_EXIST);
        }

    }

    @Override
    @Transactional
    public void updateFolder(Long folderId, String folderName) throws Throwable {
        var folder = folderRepository.findByIdAndUser(folderId, AuthHelper.getUserId())
                .orElseThrow(() -> new BusinessException(StatusCode.FOLDER_NOT_FOUND));
        if (folder.getName().equals(folderName)) {
            throw new BusinessException(StatusCode.FOLDER_ALREADY_EXIST);
        }
        folder.setName(folderName);
        folderRepository.save(folder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId) throws Throwable {
        var folder = folderRepository.findByIdAndUser(folderId, AuthHelper.getUserId())
                .orElseThrow(() -> new BusinessException(StatusCode.FOLDER_NOT_FOUND));
        folder.setStatus(Status.DISABLE);
        folderRepository.save(folder);
    }

    @Override
    @Transactional
    public void addFileToFolder(Long folderId, List<Long> fileId) throws Throwable {

        var files = fileRepository.findAllById(fileId);
        if (files.isEmpty()) {
            throw new BusinessException(StatusCode.NOT_FOUND);
        }
        if(folderId != 0){
            folderRepository.findAllByFolderIdAndStatus(folderId, Status.NORMAL)
                    .forEach(f -> {
                        f.setFileId(files.get(0).getId());
                        folderRepository.save(f);
                    });
        } else {
            files.forEach(f -> {
                f.setFolderId(folderId);
                fileRepository.save(f);
            });
        }

    }
}
