package mobile.model.payload.request.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterUsageRequest {
    protected String userId;
    protected String characterId;
    protected String date; // YYYY-MM-DD
    protected String skill; // Tên kỹ năng
}
