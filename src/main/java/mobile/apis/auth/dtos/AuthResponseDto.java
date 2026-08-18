package mobile.apis.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String imageUrl;
    private LocalDate birthday;
    private String accessToken;
    private String refreshToken;
    private Collection<String> roles;
}
