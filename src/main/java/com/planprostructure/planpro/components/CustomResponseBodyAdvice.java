package com.planprostructure.planpro.components;

import com.planprostructure.planpro.components.common.api.ApiResponse;
import com.planprostructure.planpro.components.common.api.Common;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import java.util.Map;
import java.util.stream.Stream;
import com.planprostructure.planpro.logging.PlanProLoggingService;

@ControllerAdvice
@RequiredArgsConstructor
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final PlanProLoggingService planProLoggingService;

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest serverHttpRequest,
                                  @NonNull ServerHttpResponse serverHttpResponse) {
        if (serverHttpRequest instanceof ServletServerHttpRequest && serverHttpResponse instanceof ServletServerHttpResponse) {
            HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
            HttpServletResponse response = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();

            if (Stream.of("/api/wb").anyMatch(request.getRequestURI()::contains)) {
                planProLoggingService.logResponse(request, response, body);
            }


        }

        HttpHeaders headers = serverHttpRequest.getHeaders();
        Map<String, String> headerMap = headers.toSingleValueMap();
        if (body instanceof ApiResponse<?>) {
            ApiResponse<?> apiResponse = (ApiResponse<?>) body;
            apiResponse.setCommon(new Common(headerMap));
            return apiResponse;
        }
        return body;
    }
}
