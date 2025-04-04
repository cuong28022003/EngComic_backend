package mobile.model.payload.request.deck;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.Entity.Deck;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeckRequest {
    protected String name;
    protected String description;
}
