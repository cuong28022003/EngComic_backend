package mobile.security;

import mobile.security.core.AppUserDetail;
import mobile.security.resolver.PrincipalResolver;

public class SecurityUtils {

    private SecurityUtils() {
        // utility class
    }

    /**
     * Lấy userId (String) của người dùng hiện tại đang đăng nhập từ SecurityContext.
     */
    public static String getCurrentUserId() {
        return PrincipalResolver.resolveUserId();
    }

    /**
     * Lấy username của người dùng hiện tại đang đăng nhập.
     */
    public static String getCurrentUsername() {
        return PrincipalResolver.resolveUsername();
    }

    /**
     * Lấy AppUserDetail của người dùng hiện tại đang đăng nhập.
     */
    public static AppUserDetail getCurrentUser() {
        return PrincipalResolver.resolveUser();
    }
}
