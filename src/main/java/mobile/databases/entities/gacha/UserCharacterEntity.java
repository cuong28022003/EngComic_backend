package mobile.databases.entities.gacha;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "user_character")
public class UserCharacterEntity {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;
    private String characterId;
    @Builder.Default
    private LocalDateTime obtainedAt = LocalDateTime.now();
}

