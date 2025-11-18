package com.planprostructure.planpro.service.file;

import com.planprostructure.planpro.components.FileManager;
import com.planprostructure.planpro.components.common.api.StatusCode;
import com.planprostructure.planpro.exception.BusinessException;
import com.planprostructure.planpro.properties.FileInfoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileInfoConfig fileInfoConfig;

    @Override
    public Object uploadImage(MultipartFile fileData) throws Exception {
        if(fileData.isEmpty()) throw new BusinessException(StatusCode.IMAGE_CANNOT_BE_EMPTY);
        String imageUrl =  FileManager.storeImage(fileData);

        Map<String, String> data = new HashMap<>();
        data.put("image_url",imageUrl);

        return data;
    }


}
