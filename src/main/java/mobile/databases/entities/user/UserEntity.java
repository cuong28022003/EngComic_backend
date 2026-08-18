package mobile.databases.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "user")
public class UserEntity {

    @MongoId(FieldType.OBJECT_ID)
    protected String id;
    protected String username;
    protected String email;

    @JsonIgnore
    protected String password;
    protected String fullName;
    protected LocalDate birthday;

    @CreatedDate
    protected Date createdate;
    protected String image;
    protected Boolean active;
    protected String status;

    @DBRef
    @Builder.Default
    protected Set<RoleEntity> roles = new HashSet<>();

    public UserEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = username;
        this.birthday = LocalDate.now();
        this.createdate = new Date();
        this.image = "";
        this.active = true;
        this.status = "None";
        this.roles = new HashSet<>();
    }
}
