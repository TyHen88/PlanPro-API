package com.planprostructure.planpro.payload.files;

import com.planprostructure.planpro.components.common.Pagination;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilesMainResponse {
    private FileResponse fileResponse;
    private Pagination pagination;

    @Builder
    public FilesMainResponse(FileResponse fileResponse, Pagination pagination) {
        this.fileResponse = fileResponse;
        this.pagination = pagination;
    }
}
