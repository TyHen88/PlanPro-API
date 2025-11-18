package com.planprostructure.planpro.components.common.api;

import com.planprostructure.planpro.components.common.Pagination;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
public class MainResposeData {
    private List<?> data;
    private Pagination pagination;

    @Builder
    public MainResposeData(List<?> data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }
}
