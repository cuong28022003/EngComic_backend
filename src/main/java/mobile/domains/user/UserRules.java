package mobile.domains.user;

import java.util.regex.Pattern;

/**
 * Pure domain rules for User validations and Economy calculations.
 */
public class UserRules {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return username.trim().length() >= 3 && username.trim().length() <= 50;
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= 6;
    }

    public static int calculateStreak(int currentStreak, boolean isContinuous) {
        return isContinuous ? currentStreak + 1 : 1;
    }

    public static boolean isValidTopupAmount(int diamond) {
        return diamond > 0;
    }
}
