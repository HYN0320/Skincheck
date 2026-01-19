package com.skincheck_backend.common.exception;

import com.skincheck_backend.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustom(CustomException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidation(Exception e) {
        return ResponseEntity.badRequest().body(ApiResponse.fail("요청 값이 올바르지 않습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception e) {
        e.printStackTrace(); // 🔥🔥🔥 이 줄이 핵심
        return ResponseEntity.internalServerError()
                .body(ApiResponse.fail("서버 오류가 발생했습니다."));
    }

}
