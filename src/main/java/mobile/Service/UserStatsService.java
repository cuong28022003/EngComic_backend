package mobile.Service;

import mobile.model.Entity.UserStats;
import mobile.model.payload.response.user.UserFullInfoResponse;
import mobile.model.payload.response.user.UserStatsResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserStatsService {
    UserStats addXp(ObjectId userId, int xpEarned);
    UserStats addDiamond(ObjectId userId, int diamondEarned);
    UserStatsResponse getStatsByUserId(ObjectId userId);
    UserStats updateStreak(ObjectId userId);
    UserStats saveUserStats(UserStats userStats);

    Page<UserFullInfoResponse> getTopUsersWithStats(Pageable pageable);

    UserStats upgradePremium(ObjectId userId, int days, int cost);
}
