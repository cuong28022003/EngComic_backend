package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "character_stats")
public class CharacterState {
    @Id
    private String id;
    private String characterId;
    private Map<String, AnimationAction> states; // String is state id
}
