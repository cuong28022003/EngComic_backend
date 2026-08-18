package mobile.databases.entities.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "report")
public class ReportEntity {

    @MongoId(FieldType.OBJECT_ID)
    protected String id;
    protected String comicId;
    protected String userId;
    protected String reason;
    @Builder.Default
    protected String status = "PENDING";

    @CreatedDate
    @Builder.Default
    protected Date createdAt = new Date();

    @LastModifiedDate
    protected Date updatedAt;
}
