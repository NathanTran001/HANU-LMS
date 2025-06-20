package fit.se2.hanulms.controller;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity
                .badRequest()
                .body("File too large! Please upload a file smaller than 15MB.");
    }
}