package mobile.model.payload.response.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.Entity.Character;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GachaPackResult {
    private String packId;
    private String series;
    private String coverImageUrl;
    private Character cardInside;
}
