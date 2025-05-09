package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported = false)
@Document(collection = "chapter")
public class Chapter {
    @Id
    protected String id;
    protected int chapterNumber;
    @DBRef
    protected Comic comic;
    protected String name;
    protected String cover;
    protected List<String> images; // Danh sách URL ảnh
    @CreatedDate
    protected Date createAt;
    @LastModifiedDate
    protected Date updateAt;
}

