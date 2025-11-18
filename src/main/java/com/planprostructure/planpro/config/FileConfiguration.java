package com.planprostructure.planpro.config;

import com.planprostructure.planpro.properties.FileInfoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileConfiguration implements WebMvcConfigurer {

    private final FileInfoConfig fileInfoConfig;

    @Autowired
    public FileConfiguration(FileInfoConfig fileInfoConfig) {
        this.fileInfoConfig = fileInfoConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/v1/image/**")
                .addResourceLocations("file:" + fileInfoConfig.getServerPath() + "/");
    }

}
