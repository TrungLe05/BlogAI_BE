package com.example.blogai.Exception;

import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.enums.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handlingRuntimeException(RuntimeException e){
        ApiResponse<Void> response = new ApiResponse<Void>();

        response.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        response.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(exception = AppException.class)
    public ResponseEntity<ApiResponse<Void>> handlingAppException(AppException e){
        ApiResponse<Void> response = new ApiResponse<>();

        response.setCode(e.getErrorCode().getCode());
        response.setMessage(e.getErrorCode().getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handlingDataIntegrityViolationException(
            DataIntegrityViolationException e) {

        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(ErrorCode.EMAIL_ALREADY_EXISTED.getCode());
        response.setMessage(ErrorCode.EMAIL_ALREADY_EXISTED.getMessage());

        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String enumKey = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(response);

    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.CONTENT_TOO_LARGE)
    public ApiResponse<Void> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        return ApiResponse.<Void>builder()
                .code(ErrorCode.FILE_TOO_LARGE.getCode())
                .message("File size exceeds the maximum allowed limit")
                .build();
    }
}
