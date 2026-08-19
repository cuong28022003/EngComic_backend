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
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "chapter")
public class ChapterEntity {

    @MongoId(FieldType.OBJECT_ID)
    protected String id;
    protected int chapterNumber;
    protected String comicId;
    protected String name;
    protected String imageUrl;
    protected List<Map<Integer, String>> pageUrls;
    @Builder.Default
    protected LocalDateTime createdAt = LocalDateTime.now();
    protected LocalDateTime updatedAt;
}

