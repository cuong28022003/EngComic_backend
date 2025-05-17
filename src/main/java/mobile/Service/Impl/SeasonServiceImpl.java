package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.SeasonService;
import mobile.Service.UserStatsService;
import mobile.model.Entity.Season;
import mobile.model.Entity.UserStats;
import mobile.repository.SeasonRepository;
import mobile.repository.UserRepository;
import mobile.repository.UserStatsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;
    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;
    private final UserStatsService userStatsService;

    @Override
    public Season createSeason(Season season) {
        season.setActive(true);
        return seasonRepository.save(season);
    }

    @Override
    public List<Season> getAllSeasons() {
        return seasonRepository.findAll();
    }

    @Override
    public Season getCurrentSeason() {
        return seasonRepository.findTopByOrderByEndDateDesc()
                .filter(season -> season.isActive())
                .orElse(null);
    }

    @Override
    public Season endCurrentSeasonAndDistributeRewards() {
        Season current = getCurrentSeason();
        if (current == null) return null;

        current.setActive(false);
        seasonRepository.save(current);

        // Phân phối phần thưởng theo rank
        List<UserStats> users = userStatsRepository.findAll();

        for (UserStats user : users) {
            UserStats newUserStats = userStatsService.addDiamond(user.getUserId(), user.getRank().getRewardDiamond());
            // Reset rank
            user.setXp(0);
            user.setReceivedSeasonReward(true);
            userStatsService.saveUserStats(user);
        }

        return current;
    }
}
