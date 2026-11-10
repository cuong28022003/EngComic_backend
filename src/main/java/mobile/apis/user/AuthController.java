package mobile.apis.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.AuthResponseDto;
import mobile.apis.user.dtos.LoginRequestDto;
import mobile.apis.user.dtos.RegisterRequestDto;
import mobile.businesses.boundaries.user.CheckEmail;
import mobile.businesses.boundaries.user.CheckUsername;
import mobile.businesses.boundaries.user.LoginUser;
import mobile.businesses.boundaries.user.RefreshToken;
import mobile.businesses.boundaries.user.RegisterUser;
import mobile.businesses.boundaries.user.VerifyToken;
import mobile.security.core.AppUserDetail;
import mobile.security.resolver.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUser registerUser;
    private final LoginUser loginUser;
    private final VerifyToken verifyToken;
    private final RefreshToken refreshToken;
    private final CheckUsername checkUsername;
    private final CheckEmail checkEmail;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid RegisterRequestDto requestDto) {
        RegisterUser.Request request = RegisterUser.Request.builder()
                .payload(requestDto)
                .build();

        RegisterUser.Response response = registerUser.execute(request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", response.getMessage(),
                "data", Map.of("email", response.getEmail())
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginRequestDto requestDto) {
        LoginUser.Request request = LoginUser.Request.builder()
                .payload(requestDto)
                .build();

        LoginUser.Response response = loginUser.execute(request);
        AuthResponseDto authData = response.getData();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", authData.getId());
        data.put("username", authData.getUsername());
        data.put("email", authData.getEmail());
        data.put("fullName", authData.getFullName() != null ? authData.getFullName() : authData.getUsername());
        data.put("imageUrl", authData.getImageUrl());
        data.put("coverUrl", authData.getCoverUrl());
        data.put("birthday", authData.getBirthday());
        data.put("accessToken", authData.getAccessToken());
        data.put("refreshToken", authData.getRefreshToken());
        data.put("roles", authData.getRoles());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "data", data
        ));
    }

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

    @PostMapping("/refreshtoken")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> body) {
        RefreshToken.Response response = refreshToken.execute(RefreshToken.Request.builder()
                .refreshToken(body != null ? body.get("refreshToken") : null)
                .build());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Token refreshed",
                "data", Map.of("accessToken", response.getAccessToken())
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

