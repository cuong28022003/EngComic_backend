package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "character_sounds")
public class CharacterSound {
    @Id
    private String id;

    private String characterId;
    private Map<String, Map<String, Sound>> sounds; //String is group, String is sample
}
