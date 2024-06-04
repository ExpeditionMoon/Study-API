package moon.recipeAndCart.util.exception;

import jakarta.persistence.EntityNotFoundException;
import moon.recipeAndCart.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 엔티티를 찾을 수 없을 때 발생
     * HTTP 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(ApiResponse.fail(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * 클라이언트의 잘못된 요청으로 발생
     * HTTP 500 Internal Server Error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ApiResponse.fail("서버 오류가 발생했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 예상치 못한 모든 예외를 처리
     * HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAllUncaughtException(Exception ex) {
        return new ResponseEntity<>(ApiResponse.fail("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 사용자 정의 예외 클래스
     * 데이터가 존재하지 않거나 비어있을 때 발생
     * HTTP 404 Not Found
     */
    @ExceptionHandler(NoContentFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNoContentFoundException(NoContentFoundException ex) {
        return new ResponseEntity<>(ApiResponse.fail(ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
