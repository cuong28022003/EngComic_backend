package mobile.apis.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private LocalDate birthday;
    private Date createdDate;
    private String imageUrl;
    private String coverUrl;
    private List<String> featuredCharacterIds;
    private Boolean active;
    private String status;
    private Set<String> roles;
}

