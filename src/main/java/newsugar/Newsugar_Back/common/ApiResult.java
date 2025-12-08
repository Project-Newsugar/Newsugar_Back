package newsugar.Newsugar_Back.common;

import java.time.Instant;

public class ApiResult<T> {
    // 공통 응답 포맷: 성공 여부
    private boolean success;
    // 공통 응답 포맷: 상태 코드 (예: OK, BAD_REQUEST 등)
    private String code;
    // 공통 응답 포맷: 사용자에게 보여줄 메시지
    private String message;
    // 공통 응답 포맷: 실제 데이터 페이로드
    private T data;
    // 공통 응답 포맷: 응답 생성 시각
    private Instant timestamp;

    public ApiResult() {}

    // 성공 응답 생성 헬퍼
    public static <T> ApiResult<T> ok(T data) {
        ApiResult<T> r = new ApiResult<>();
        r.success = true;
        r.code = ErrorCode.OK.name();
        r.data = data;
        r.timestamp = Instant.now();
        return r;
    }

    // 오류 응답 생성 헬퍼
    public static <T> ApiResult<T> error(String code, String message) {
        ApiResult<T> r = new ApiResult<>();
        r.success = false;
        r.code = code;
        r.message = message;
        r.timestamp = Instant.now();
        return r;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}

