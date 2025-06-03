package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported = false)
@Document(collection = "chapter")
public class Chapter {
    @Id
    protected ObjectId id;
    protected int chapterNumber;
    protected ObjectId comicId;
    protected String name;
    protected String imageUrl;
    protected List<Map<Integer,String>> pageUrls; // Danh sách URL ảnh
    protected LocalDateTime createdAt = LocalDateTime.now();
    protected LocalDateTime updatedAt;
}

