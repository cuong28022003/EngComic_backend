package mobile.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    protected String id;
    protected String deckId;
    protected String front;
    protected String back;
    protected String IPA;
    protected String image;
    protected String audio;
    protected Set<String> tags;
    protected String createAt;
    protected String updateAt;
}
