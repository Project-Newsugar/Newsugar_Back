package newsugar.common.exception;

import lombok.extern.slf4j.Slf4j;
import newsugar.common.response.ApiResponse;
import newsugar.common.response.ResponseCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {

        log.error("API Exception: {}", ex.getMessage());

        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(ApiResponse.error(
                        ResponseCode.INVALID_REQUEST,
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {

        log.error("Unexpected Exception", ex);

        return ResponseEntity
                .status(500)
                .body(ApiResponse.error(
                        ResponseCode.SERVER_ERROR,
                        "서버 오류가 발생했습니다."
                ));
    }
}
