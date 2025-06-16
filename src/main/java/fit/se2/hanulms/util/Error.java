package fit.se2.hanulms.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Component
public class Error {
    public List<String> getErrorMessages(BindingResult result) {
        List<String> errors = new ArrayList<>();
        result.getFieldErrors().forEach(error ->
                errors.add(error.getDefaultMessage())
        );
        return errors;
    }
}
