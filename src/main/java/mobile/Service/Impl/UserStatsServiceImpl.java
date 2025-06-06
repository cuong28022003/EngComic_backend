package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.RankService;
import mobile.Service.UserService;
import mobile.Service.UserStatsService;
import mobile.mapping.UserMapping;
import mobile.model.Entity.Rank;
import mobile.model.Entity.User;
import mobile.model.Entity.UserStats;
import mobile.model.payload.response.user.UserFullInfoResponse;
import mobile.model.payload.response.user.UserStatsResponse;
import mobile.repository.UserRepository;
import mobile.repository.UserStatsRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {
    private final UserMapping userMapping;
    private final UserRepository userRepository;

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
    public UserStatsResponse getStatsByUserId(ObjectId userId) {
        UserStats userStats = userStatsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserStats newStats = new UserStats();
                    newStats.setUserId(userId);
                    return userStatsRepository.save(newStats);
                });
        if (userStats.getLastStudyDate().isEqual(LocalDate.now())) {
            userStats.setCurrentStreak(userStats.getCurrentStreak());
        } else if (userStats.getLastStudyDate().isEqual(LocalDate.now().minusDays(1))) {
            userStats.setCurrentStreak(userStats.getCurrentStreak());
        } else {
            userStats.setCurrentStreak(0);
        }
        userStatsRepository.save(userStats);
        return userMapping.mapToUserStatsResponse(userStats);
    }

    @Override
    public Page<UserFullInfoResponse> getTopUsersWithStats(Pageable pageable) {
        Page<UserStats> topStats = userStatsRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "xp"))
        );
        Page<UserFullInfoResponse> topUsers = topStats.map(userStats -> {
            User user = userRepository.findById(userStats.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userStats.getUserId()));
            return userMapping.toUserFullInfoResponse(user, userStats);
        });
        return topUsers;
    }

    @Override
    public UserStats upgradePremium(ObjectId userId, int days, int cost) {
        // Calculate required diamonds
        if (cost == 0) {
            throw new IllegalArgumentException("Invalid duration.");
        }

        // Fetch user stats
        UserStats userStats = userStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (userStats.getDiamond() < cost) {
            throw new RuntimeException("Not enough diamonds.");
        }

        // Deduct diamonds and upgrade to premium
        userStats.setDiamond(userStats.getDiamond() - cost);
        // Update premium status and expiration date
        LocalDateTime currentExpiration = userStats.getPremiumExpiredAt();
        if (currentExpiration != null && currentExpiration.isAfter(LocalDateTime.now())) {
            // Add days to the existing expiration date
            userStats.setPremiumExpiredAt(currentExpiration.plusDays(days));
        } else {
            // Set a new expiration date starting from now
            userStats.setPremiumExpiredAt(LocalDateTime.now().plusDays(days));
        }
        userStats.setPremium(true);

        // Save updated user stats
        userStatsRepository.save(userStats);

        return userStats;
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
