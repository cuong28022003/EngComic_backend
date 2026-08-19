package mobile.databases.entities.user;

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
@Document(collection = "season")
public class SeasonEntity {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String name;
    private int seasonNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
}

