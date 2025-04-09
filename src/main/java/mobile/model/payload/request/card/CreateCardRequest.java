package mobile.model.payload.request.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequest {
    protected String front;
    protected String back;
    protected String IPA;
    protected String image;
    protected String audio;
    protected Set<String> tags = new HashSet<>();
    protected String deckId;
}
