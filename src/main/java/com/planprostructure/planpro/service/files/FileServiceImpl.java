package com.planprostructure.planpro.service.files;

import com.planprostructure.planpro.domain.files.FileRepository;
import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;

import java.awt.print.Pageable;

@Service
@RequiredArgsConstructor
public class FileServiceImpl  implements FileService{
    private final FileRepository fileRepository;

    @Override
    public Object getFiles(Pageable pageable) throws Throwable {
        return null;
    }
}
