package mobile.model.payload.response.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.Entity.Character;
import mobile.model.Entity.Pack;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCharacterResponse {
    private Character character;
    private Pack pack;
    private LocalDateTime obtainedAt; // ngày nhận thẻ
}
