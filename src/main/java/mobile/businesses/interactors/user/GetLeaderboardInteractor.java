package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.LeaderboardEntryDto;
import mobile.businesses.boundaries.user.GetLeaderboard;
import mobile.databases.entities.user.RankEntity;
import mobile.databases.entities.user.UserEntity;
import mobile.databases.entities.user.UserStatsEntity;
import mobile.databases.repositories.user.RankRepository;
import mobile.databases.repositories.user.UserRepository;
import mobile.databases.repositories.user.UserStatsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GetLeaderboardInteractor implements GetLeaderboard {

    private static final int MAX_PAGE_SIZE = 50;

    private final UserStatsRepository userStatsRepository;
    private final UserRepository userRepository;
    private final RankRepository rankRepository;

    @Override
    public Response execute(Request request) {
        int pageIndex = Math.max(request.getPage(), 0);
        int pageSize = Math.min(Math.max(request.getSize(), 1), MAX_PAGE_SIZE);

        String sortField = "streak".equalsIgnoreCase(request.getSort()) ? "longestStreak" : "xp";
        Sort sort = Sort.by(Sort.Direction.DESC, sortField)
                .and(Sort.by(Sort.Direction.DESC, "xp"));
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        Page<UserStatsEntity> statsPage = userStatsRepository.findByXpGreaterThan(0, pageable);

        List<String> userIds = statsPage.getContent().stream()
                .map(UserStatsEntity::getUserId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();

        Map<String, UserEntity> usersById = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        List<RankEntity> ranks = rankRepository.findAll(Sort.by(Sort.Direction.ASC, "minXp"));

        LocalDate today = LocalDate.now();
        int offset = pageIndex * pageSize;

        List<LeaderboardEntryDto> entries = IntStream.range(0, statsPage.getContent().size())
                .mapToObj(index -> toEntry(
                        statsPage.getContent().get(index),
                        usersById,
                        ranks,
                        offset + index + 1,
                        today))
                .toList();

        Page<LeaderboardEntryDto> result = new PageImpl<>(entries, pageable, statsPage.getTotalElements());
        return Response.builder().result(result).build();
    }

    private LeaderboardEntryDto toEntry(
            UserStatsEntity stats,
            Map<String, UserEntity> usersById,
            List<RankEntity> ranks,
            int rank,
            LocalDate today) {
        UserEntity user = usersById.get(stats.getUserId());
        RankEntity rankEntity = resolveRank(ranks, stats);

        boolean hasFullName = user != null && user.getFullName() != null && !user.getFullName().isBlank();
        boolean studiedToday = stats.getLastStudyDate() != null && stats.getLastStudyDate().equals(today);

        return LeaderboardEntryDto.builder()
                .rank(rank)
                .userId(stats.getUserId())
                .fullName(hasFullName ? user.getFullName() : "Độc giả ẩn danh")
                .username(user != null ? user.getUsername() : null)
                .imageUrl(user != null ? user.getImage() : null)
                .xp(stats.getXp())
                .diamond(stats.getDiamond())
                .rankName(rankEntity != null && rankEntity.getName() != null ? rankEntity.getName() : "Trainee")
                .rankBadge(rankEntity != null ? rankEntity.getBadge() : null)
                .currentStreak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .premium(stats.isPremium())
                .studiedToday(studiedToday)
                .build();
    }

    private RankEntity resolveRank(List<RankEntity> ranks, UserStatsEntity stats) {
        RankEntity matched = null;
        for (RankEntity rank : ranks) {
            if (stats.getXp() >= rank.getMinXp()) {
                matched = rank;
            }
        }
        return matched != null ? matched : stats.getRank();
    }
}
