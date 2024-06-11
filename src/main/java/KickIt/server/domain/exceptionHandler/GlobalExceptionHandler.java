package KickIt.server.domain.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

// 전역 예외 처리를 위한 handler
@ControllerAdvice
public class GlobalExceptionHandler {
    // resource not found exception 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundExceptions(ResourceNotFoundException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.NOT_FOUND.value());
        responseBody.put("message", "resource 발견 안 됨");
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    // bad request exception 처리
    // 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("message", "유효성 검사 실패");
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
    // 입력 파라미터 타입 기댓값과 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchExceptions(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("message", "입력 파라미터 타입 오류");
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
    // 필수 입력 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("message", "입력 파라미터 누락: " + ex.getParameterName());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    //  invalid url 요청 처리
    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<Map<String, Object>> handleURISyntaxException(URISyntaxException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST);
        responseBody.put("message", "invalid한 url 접근: " + ex.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    //  invalid url or parameter 요청 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST);
        responseBody.put("message", "Invalid URL 접근 혹은 invalid parameters 사용: " + ex.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    // internal server error exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralExceptions(Exception ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        responseBody.put("message", "서버 자체 오류: " + ex.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
