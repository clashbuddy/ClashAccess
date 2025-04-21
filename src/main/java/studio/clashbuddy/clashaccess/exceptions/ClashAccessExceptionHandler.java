package studio.clashbuddy.clashaccess.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ClashAccessExceptionHandler {


    @ExceptionHandler(ClashAccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(ClashAccessDeniedException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatus());
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status);
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());
        body.put("timestamp", Instant.now().toString());

        return ResponseEntity.status(status).body(body);
    }

}
