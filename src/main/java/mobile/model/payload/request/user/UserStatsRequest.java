package mobile.model.payload.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatsRequest {
    protected String userId;
    protected int xp;
}
