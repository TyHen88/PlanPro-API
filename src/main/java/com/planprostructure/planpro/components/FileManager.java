package com.planprostructure.planpro.components;

import com.planprostructure.planpro.logging.AppLogManager;
import com.planprostructure.planpro.properties.FileInfoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Component
public class FileManager {

    public static FileInfoConfig fileInfoConfig;

    @Autowired
    public void setFileInfoConfig(FileInfoConfig fileInfoConfig) {
        this.fileInfoConfig = fileInfoConfig;
    }

    /**
     * Method storeImage : Handle the process for storing images on server
     * @param files MultipartFile
     * @return File object
     * @throws Exception
     */
    public static String storeImage(MultipartFile files) throws Exception {

        final String fileName = UUID.randomUUID() + "." + files.getOriginalFilename().substring(files.getOriginalFilename().lastIndexOf(".") +  1);
        String targetPath = fileInfoConfig.getServerPath();
        File targetFile = new File(Paths.get(targetPath).toUri());

        if(!targetFile.exists())
            targetFile.mkdirs();

        File targetImage = new File(Paths.get(targetPath, fileName).toUri());

        if(targetImage.exists())
            targetImage.delete();

        Files.copy(files.getInputStream(),targetImage.toPath());

        return fileName;
    }

    /**
     * Method storeImageFromBase64: Handle the process for storing images from base64 string on server
     *
     * @param base64Image String containing the base64 encoded image
     * @param fileName    The desired file name
     * @return The saved file name
     * @throws Exception
     */
    public static String storeImageFromBase64(String fileName, String base64Image) throws Exception {
        try {
            // Sanitize file name
            final String sanitizedFileName = Paths.get(fileName).getFileName().toString();
            String targetPath = fileInfoConfig.getTempPath();
            File targetDir = new File(targetPath);

            if (!targetDir.exists()) targetDir.mkdirs();

            File targetImage = new File(targetDir, sanitizedFileName);

            if (targetImage.exists()) return sanitizedFileName;

            // Remove data URL prefix if present
            String base64Data = base64Image.replaceFirst("^data:image/\\w+;base64,", "");
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            Files.write(targetImage.toPath(), imageBytes);

            return sanitizedFileName;
        } catch (IOException e) {
            AppLogManager.error("Error writing image to file: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            AppLogManager.error("Invalid Base64 data: " + e.getMessage());
            throw e;
        }

    }

}
