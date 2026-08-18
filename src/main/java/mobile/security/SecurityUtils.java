package mobile.security;

import mobile.security.DTO.AppUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    private SecurityUtils() {
        // utility class
    }

    /**
     * Lấy userId (String) của người dùng hiện tại đang đăng nhập từ SecurityContext.
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AppUserDetail userDetail) {
            return userDetail.getId() != null ? userDetail.getId().toHexString() : null;
        }
        return null;
    }

    /**
     * Lấy username của người dùng hiện tại đang đăng nhập.
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String principalString) {
            return principalString;
        }
        return authentication.getName();
    }
}
