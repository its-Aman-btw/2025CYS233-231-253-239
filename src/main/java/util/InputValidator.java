package util;

import java.util.regex.Pattern;

public class InputValidator {

    // 1. Check if field is empty (Yeh method miss ho raha tha)
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    // 2. Regular Expression for Email validation
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) return false;
        return pat.matcher(email).matches();
    }

    // 3. Phone Number validation (Strict 11 digits format check)
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{11}");
    }
}
