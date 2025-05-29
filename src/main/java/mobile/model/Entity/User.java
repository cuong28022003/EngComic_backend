package mobile.model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported=false)
@Document(collection = "user")
public class User {
    @JsonIgnore

    @Id
    protected  ObjectId id;
    protected  String username;
    protected  String email;
    @JsonIgnore
    protected  String password;
    protected  String fullName;
    protected LocalDate birthday;
    @CreatedDate
    protected   Date createdate;
    protected  String image;
    protected Boolean active;
    protected  String status;

    @DBRef
//    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    protected  Set<Role> roles = new HashSet<>();

    public User(String username, String email, String password) {
        this.id = new ObjectId();
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = username;
        this.birthday = LocalDate.now();
        this.createdate = new Date();
        this.image ="";
        this.active = true; // skip send active email
        this.status="None";
    }
}