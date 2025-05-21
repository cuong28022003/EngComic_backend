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

        Rank rank = rankService.getRankByXp(stats.getXp());
        stats.setRank(rank);

        LocalDate today = LocalDate.now();
        LocalDate lastStudyDate = stats.getLastStudyDate();

        if (lastStudyDate.isEqual(today)) {
            stats.setCurrentStreak(stats.getCurrentStreak());
        } else if (lastStudyDate.isEqual(today.minusDays(1))) {
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        } else {
            stats.setCurrentStreak(1);
        }

        if (stats.getCurrentStreak() > stats.getLongestStreak()) {
            stats.setLongestStreak(stats.getCurrentStreak());
        }
        stats.setLastStudyDate(today);

        return userStatsRepository.save(stats);
    }

    @Override
    public UserStats addDiamond(ObjectId userId, int diamondEarned) {
        return userStatsRepository.findByUserId(userId)
                .map(stats -> {
                    stats.setDiamond(stats.getDiamond() + diamondEarned);
                    return userStatsRepository.save(stats);
                })
                .orElseThrow(() -> new RuntimeException("UserStats not found"));
    }

    @Override
    public UserStats getStatsByUserId(ObjectId userId) {
        return userStatsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserStats newStats = new UserStats();
                    newStats.setUserId(userId);
                    newStats.setRank(rankService.getRankByXp(0));
                    return userStatsRepository.save(newStats);
                });
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

    @Override
    public UserStats updateStreak(ObjectId userId) {
        UserStats stats = userStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("UserStats not found"));

        LocalDate today = LocalDate.now();
        LocalDate lastStudyDate = stats.getLastStudyDate();

        if (lastStudyDate != null) {
            if (lastStudyDate.isEqual(today)) {
                return stats;
            } else if (lastStudyDate.isEqual(today.minusDays(1))) {
                stats.setCurrentStreak(stats.getCurrentStreak() + 1);
            } else {
                stats.setCurrentStreak(1);
            }
        } else {
            // Nếu lastStudyDate chưa được thiết lập, khởi tạo streak
            stats.setCurrentStreak(0);
        }

        if (stats.getCurrentStreak() > stats.getLongestStreak()) {
            stats.setLongestStreak(stats.getCurrentStreak());
        }
        stats.setLastStudyDate(today);
        return userStatsRepository.save(stats);
    }

    @Override
    public UserStats saveUserStats(UserStats userStats) {
        return userStatsRepository.save(userStats);
    }
}
