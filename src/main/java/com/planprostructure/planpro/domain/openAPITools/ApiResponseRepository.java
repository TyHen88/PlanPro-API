package com.planprostructure.planpro.domain.openAPITools;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiResponseRepository extends JpaRepository<ApiResponse, Long> {
    List<ApiResponse> findByApiRequestId(Long requestId);
}
