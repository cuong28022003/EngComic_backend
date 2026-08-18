package mobile.apis.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobile.apis.auth.dtos.AuthResponseDto;
import mobile.apis.auth.dtos.LoginRequestDto;
import mobile.apis.auth.dtos.RegisterRequestDto;
import mobile.businesses.boundaries.auth.LoginUser;
import mobile.businesses.boundaries.auth.RegisterUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUser registerUser;
    private final LoginUser loginUser;

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

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "data", Map.of(
                        "id", authData.getId(),
                        "username", authData.getUsername(),
                        "email", authData.getEmail(),
                        "fullName", authData.getFullName() != null ? authData.getFullName() : authData.getUsername(),
                        "accessToken", authData.getAccessToken(),
                        "refreshToken", authData.getRefreshToken(),
                        "roles", authData.getRoles()
                )
        ));
    }
}
