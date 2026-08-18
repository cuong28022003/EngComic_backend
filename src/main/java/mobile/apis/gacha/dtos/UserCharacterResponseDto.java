package mobile.apis.gacha.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCharacterResponseDto {
    private String id;
    private String userId;
    private String characterId;
    private LocalDateTime obtainedAt;
    private CharacterResponseDto character;
}
