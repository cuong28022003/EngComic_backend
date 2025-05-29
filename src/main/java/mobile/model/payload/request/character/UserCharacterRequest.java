package mobile.model.payload.request.character;

import lombok.Data;

@Data
public class UserCharacterRequest {
    private String userId;
    private String characterId;
}
