package mobile.model.payload.request.user;

import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangePassRequest {
    @NotEmpty
    String password;

    @NotEmpty(message = "Thiếu password")
    @Size(min = 8, message = "Password phải từ 8 kí tự trở lên")
    String newPassword;
}
