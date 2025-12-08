package newsugar.Newsugar_Back.common;

import java.time.Instant;

public class ApiResult<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    private Instant timestamp;

    public ApiResult() {}

    public static <T> ApiResult<T> ok(T data) {
        ApiResult<T> r = new ApiResult<>();
        r.success = true;
        r.code = ErrorCode.OK.name();
        r.data = data;
        r.timestamp = Instant.now();
        return r;
    }

    public static <T> ApiResult<T> error(String code, String message) {
        ApiResult<T> r = new ApiResult<>();
        r.success = false;
        r.code = code;
        r.message = message;
        r.timestamp = Instant.now();
        return r;
    }

    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public Instant getTimestamp() { return timestamp; }
}
