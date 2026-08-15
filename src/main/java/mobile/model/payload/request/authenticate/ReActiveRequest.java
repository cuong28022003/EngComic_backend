package mobile.model.payload.request.authenticate;

import lombok.*;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReActiveRequest {
    @NotEmpty
    String email;
}
