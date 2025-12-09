package newsugar.Newsugar_Back.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 요청 유효성 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Void>> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResult.error(ErrorCode.BAD_REQUEST.name(), "유효성 오류"));
    }

    // 잘못된 인자 전달 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResult.error(ErrorCode.BAD_REQUEST.name(), ex.getMessage()));
    }

    // 기타 예상하지 못한 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleException(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(ApiResult.error(ErrorCode.INTERNAL_ERROR.name(), "서버 오류"));
    }
}

