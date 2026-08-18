package mobile.databases.entities.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "comment")
public class CommentEntity {

    @JsonIgnore
    @MongoId(FieldType.OBJECT_ID)
    protected String id;
    protected String parentId;
    @Builder.Default
    protected List<CommentEntity> listChild = new ArrayList<>();
    protected String comicUrl;
    protected String userId;
    @Builder.Default
    protected int numChild = 0;
    @Builder.Default
    protected int depth = 0;
    protected String content;
    @Builder.Default
    protected Date createdate = new Date();
}
