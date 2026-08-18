package mobile.databases.entities.rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "rank")
public class RankEntity {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String name;
    private int minXp;
    private int maxXp;
    private String badge;
    private int rewardDiamond;
    private String rewardCharacterId;
}
