package mobile.databases.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.databases.entities.user.RankEntity;
import mobile.databases.repositories.user.RankRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Đồng bộ collection "rank" với 9 bậc chuẩn (upsert theo tên) mỗi khi backend khởi động.
 * Badge trỏ tới ảnh tĩnh trong front-end: /images/badges/*.png
 * Idempotent: chạy lại không tạo trùng, chỉ cập nhật minXp/maxXp/badge.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RankDataSeeder implements ApplicationRunner {

    private final RankRepository rankRepository;

    private static final List<RankSeed> RANKS = List.of(
            new RankSeed("Trainee", 0, 99, "/images/badges/trainee.png", 0),
            new RankSeed("Black Iron", 100, 299, "/images/badges/black_iron.png", 5),
            new RankSeed("Bronze", 300, 699, "/images/badges/bronze.png", 10),
            new RankSeed("Silver", 700, 1499, "/images/badges/silver.png", 20),
            new RankSeed("Gold", 1500, 2999, "/images/badges/gold.png", 40),
            new RankSeed("Platinum", 3000, 4999, "/images/badges/platinum.png", 70),
            new RankSeed("Diamond", 5000, 7999, "/images/badges/diamond.png", 120),
            new RankSeed("Master", 8000, 11999, "/images/badges/master.png", 200),
            new RankSeed("King", 12000, Integer.MAX_VALUE, "/images/badges/king.png", 350)
    );

    @Override
    public void run(ApplicationArguments args) {
        int created = 0;
        int updated = 0;
        int removed = 0;

        List<String> validNames = RANKS.stream().map(RankSeed::name).toList();

        for (RankEntity existing : rankRepository.findAll()) {
            if (existing.getName() == null || !validNames.contains(existing.getName())) {
                rankRepository.delete(existing);
                removed++;
            }
        }

        for (RankSeed seed : RANKS) {
            RankEntity entity = rankRepository.findByName(seed.name()).orElse(null);
            if (entity == null) {
                rankRepository.save(RankEntity.builder()
                        .name(seed.name())
                        .minXp(seed.minXp())
                        .maxXp(seed.maxXp())
                        .badge(seed.badge())
                        .rewardDiamond(seed.rewardDiamond())
                        .rewardCharacterId(null)
                        .build());
                created++;
            } else {
                boolean changed = false;
                if (entity.getMinXp() != seed.minXp()) {
                    entity.setMinXp(seed.minXp());
                    changed = true;
                }
                if (entity.getMaxXp() != seed.maxXp()) {
                    entity.setMaxXp(seed.maxXp());
                    changed = true;
                }
                if (!seed.badge().equals(entity.getBadge())) {
                    entity.setBadge(seed.badge());
                    changed = true;
                }
                if (entity.getRewardDiamond() != seed.rewardDiamond()) {
                    entity.setRewardDiamond(seed.rewardDiamond());
                    changed = true;
                }
                if (changed) {
                    rankRepository.save(entity);
                    updated++;
                }
            }
        }

        log.info("RankDataSeeder: {} rank mới, {} rank cập nhật, {} rank cũ đã xoá.", created, updated, removed);
    }

    private record RankSeed(String name, int minXp, int maxXp, String badge, int rewardDiamond) {
    }
}
