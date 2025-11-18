package com.planprostructure.planpro.payload.files;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FileRequest {

    private String newFileName; // only for renaming files


    @Builder
    public FileRequest(String newFileName) {
        this.newFileName = newFileName;
    }
}
