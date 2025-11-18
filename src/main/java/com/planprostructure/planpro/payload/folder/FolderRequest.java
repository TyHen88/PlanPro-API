package com.planprostructure.planpro.payload.folder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FolderRequest {
    private String name;
    @JsonProperty("file_id")
    private List<Long> fileId;

}
