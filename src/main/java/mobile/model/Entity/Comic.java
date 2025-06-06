package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RestResource(exported = false)
@Document(collection = "comic")
public class Comic {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    protected ObjectId id;
    protected String name;
    protected String url;
    protected String description;
    protected String artist;
    protected String genre;
    protected String imageUrl;
    protected String backgroundUrl;
    protected ObjectId uploaderId;
    protected int views;
    protected String status = "NONE";
    protected LocalDateTime createdAt = LocalDateTime.now();
    protected LocalDateTime updateAt;

}