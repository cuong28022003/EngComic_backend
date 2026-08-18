package mobile.security.resolver;

import mobile.security.core.AppUserDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class PrincipalResolver {

    /**
     * Trích xuất User ID (String) từ Authentication hoặc SecurityContextHolder.
     */
    public static String resolveUserId() {
        return resolveUserId(SecurityContextHolder.getContext().getAuthentication());
    }

    public static String resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AppUserDetail userDetail) {
            return userDetail.getId();
        }
        return null;
    }

    /**
     * Yêu cầu User ID phải tồn tại (Authenticated), nếu không sẽ throw AccessDeniedException.
     */
    public static String requireUserId() {
        String userId = resolveUserId();
        if (userId == null) {
            throw new AccessDeniedException("User authentication is required");
        }
        return userId;
    }

    /**
     * Trích xuất Username từ Authentication.
     */
    public static String resolveUsername() {
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

    /**
     * Trích xuất toàn bộ đối tượng AppUserDetail từ Authentication.
     */
    public static AppUserDetail resolveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetail userDetail) {
            return userDetail;
        }
        return null;
    }
}
