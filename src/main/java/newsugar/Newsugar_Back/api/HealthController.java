package newsugar.Newsugar_Back.api;

import newsugar.Newsugar_Back.common.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
public class HealthController {

    // 헬스 체크
    @Operation(summary = "헬스 체크", description = "서비스가 정상 동작하는지 확인")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = ApiResult.class)))
    @GetMapping("/health")
    public ResponseEntity<ApiResult<String>> health() {
        return ResponseEntity.ok(ApiResult.ok("ok"));
    }
}

