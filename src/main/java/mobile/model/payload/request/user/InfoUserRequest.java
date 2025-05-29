package mobile.model.payload.request.user;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InfoUserRequest {
    protected  String fullName;
    protected LocalDate birthday;
    protected  String image;

}
