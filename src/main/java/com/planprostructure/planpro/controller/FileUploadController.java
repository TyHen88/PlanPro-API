package com.planprostructure.planpro.controller;


import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.service.file.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/wb/v1")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "File Upload API")
public class FileUploadController extends ProPlanRestController {

    private final FileUploadService fileUploadService;

    @PostMapping("/files/upload-image")
    public ResponseEntity<?> uploadImage(
            @Valid @NotNull @RequestPart(name= "file_data") MultipartFile fileData
    ) throws Exception{
        return ok(fileUploadService.uploadImage(fileData));
    }


}
