package mobile.databases.entities.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "character_sounds")
public class CharacterSoundEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private String characterId;

    private Map<String, Object> sounds;
}
