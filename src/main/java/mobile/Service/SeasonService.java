package mobile.Service;

import mobile.model.Entity.Season;

import java.util.List;

public interface SeasonService {
    Season createSeason(Season season);
    List<Season> getAllSeasons();
    Season getCurrentSeason();
    Season endCurrentSeasonAndDistributeRewards();
}
