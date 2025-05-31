package mobile.model.payload.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFullInfoResponse {
    UserResponse user;
    UserStatsResponse userStats;
}
