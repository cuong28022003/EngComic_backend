package mobile.apis.user;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.user.CheckEmail;
import mobile.businesses.boundaries.user.CheckUsername;
import mobile.businesses.boundaries.user.SyncKeycloakUser;
import mobile.businesses.boundaries.user.VerifyToken;
import mobile.security.core.AppUserDetail;
import mobile.security.resolver.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final VerifyToken verifyToken;
    private final CheckUsername checkUsername;
    private final CheckEmail checkEmail;
    private final SyncKeycloakUser syncKeycloakUser;

    @GetMapping("/verifytoken")
    public ResponseEntity<Map<String, Object>> verifyToken(@CurrentUser(required = false) AppUserDetail currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid or expired token"
            ));
        }

        VerifyToken.Response response = verifyToken.execute(VerifyToken.Request.builder()
                .userId(currentUser.getId())
                .build());

        if (!response.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid or expired token"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Token is valid",
                "data", response.getUser()
        ));
    }

    @PostMapping("/keycloak/sync")
    public ResponseEntity<Map<String, Object>> syncKeycloak(@CurrentUser(required = false) AppUserDetail currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Missing or invalid Keycloak token"
            ));
        }

        SyncKeycloakUser.Response response = syncKeycloakUser.execute(SyncKeycloakUser.Request.builder()
                .userId(currentUser.getId())
                .build());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Keycloak user synced",
                "data", response.getData()
        ));
    }

    @GetMapping("/checkusername")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam(required = false) String username) {
        CheckUsername.Response response = checkUsername.execute(CheckUsername.Request.builder()
                .username(username)
                .build());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", response.isValid() ? "Username is available" : "Username already taken",
                "data", Map.of("valid", response.isValid())
        ));
    }

    @GetMapping("/checkemail")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam(required = false) String email) {
        CheckEmail.Response response = checkEmail.execute(CheckEmail.Request.builder()
                .email(email)
                .build());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", response.isValid() ? "Email is available" : "Email already taken",
                "data", Map.of("valid", response.isValid())
        ));
    }
}
