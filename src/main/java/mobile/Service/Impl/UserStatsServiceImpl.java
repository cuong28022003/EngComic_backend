package mobile.Service.Impl;

import mobile.Service.RankService;
import mobile.Service.UserService;
import mobile.Service.UserStatsService;
import mobile.mapping.UserMapping;
import mobile.model.Entity.Rank;
import mobile.model.Entity.User;
import mobile.model.Entity.UserStats;
import mobile.model.payload.response.user.UserStatsResponse;
import mobile.repository.UserStatsRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserStatsServiceImpl implements UserStatsService {

    @Autowired
    private UserStatsRepository userStatsRepository;

    @Autowired
    private RankService rankService;

    @Autowired
    private UserService userService;

    @Override
    public UserStats addXp(ObjectId userId, int xpEarned) {
        UserStats stats = userStatsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserStats newStats = new UserStats();
                    newStats.setUserId(userId);
                    return newStats;
                });

        stats.setXp(stats.getXp() + xpEarned);

        // Xử lý streak
        LocalDate today = LocalDate.now();
        if (stats.getLastStudyDate() != null) {
            LocalDate lastActiveDate = stats.getLastStudyDate();
            if (lastActiveDate.plusDays(1).equals(today)) {
                // Ngày liên tiếp
                stats.setCurrentStreak(stats.getCurrentStreak() + 1);
            } else if (!lastActiveDate.equals(today)) {
                // Không liên tiếp, reset streak
                stats.setCurrentStreak(1);
            }
        } else {
            // Lần đầu tiên
            stats.setCurrentStreak(1);
        }

        // Cập nhật longest streak
        if (stats.getCurrentStreak() > stats.getLongestStreak()) {
            stats.setLongestStreak(stats.getCurrentStreak());
        }

        Rank rank = rankService.getRankByXp(stats.getXp());
        stats.setRank(rank);

        return userStatsRepository.save(stats);
    }

    @Override
    public UserStats getStatsByUserId(ObjectId userId) {
        return userStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("UserStats not found"));
    }

    @Override
    public Page<UserStatsResponse> getTopUsersWithStats(int limit) {
        Page<UserStats> topStats = userStatsRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "xp")));
        Page<UserStatsResponse> topUsers = topStats.map(userStats -> {
            User user = userService.findById(userStats.getUserId());
            return UserMapping.mapToUserStatsResponse(user, userStats);
        });
        return topUsers;
    }
}
