package mobile.databases.entities.comic;

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
@Document(collection = "comic")
public class ComicEntity {

    @MongoId(FieldType.OBJECT_ID)
    protected String id;
    protected String name;
    protected String url;
    protected String description;
    protected String artist;
    protected String genre;
    protected String imageUrl;
    protected String backgroundUrl;
    protected String uploaderId;
    @Builder.Default
    protected int views = 0;
    @Builder.Default
    protected String status = "PENDING";
    @Builder.Default
    protected LocalDateTime createdAt = LocalDateTime.now();
    protected LocalDateTime updateAt;

    protected String englishLevel;
    protected String ageRating; // "ALL", "13+", "16+", "18+"
}
