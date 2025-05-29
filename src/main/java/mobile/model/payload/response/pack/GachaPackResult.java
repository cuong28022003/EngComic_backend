package mobile.model.payload.response.pack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.Entity.Character;
import mobile.model.Entity.Pack;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GachaPackResult {
    private Pack pack;
    private Character character;
}
