package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CloudinaryService;
import mobile.Service.RankService;
import mobile.mapping.RankMapping;
import mobile.model.Entity.Rank;
import mobile.model.payload.request.rank.CreateRankRequest;
import mobile.model.payload.response.rank.RankResponse;
import mobile.repository.RankRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {
    private final RankRepository rankRepository;

    private final RankMapping rankMapping;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CloudinaryService cloudinaryService;


    @Override
    public List<RankResponse> getAllRankWithCharacterAndPack() {
        Aggregation aggregation = Aggregation.newAggregation(
                // 1. JOIN rank.rewardCharacterId -> character._id
                Aggregation.lookup("character", "rewardCharacterId", "_id", "rewardCharacter"),
                Aggregation.unwind("rewardCharacter"), // Chắc chắn mỗi rank chỉ có 1 character

                // 2. JOIN rewardCharacter.packId -> pack._id
                Aggregation.lookup("pack", "rewardCharacter.packId", "_id", "rewardCharacter.pack"),
                Aggregation.unwind("rewardCharacter.pack"), // Nếu luôn có 1 pack

                // 3. Sắp xếp theo minXp giảm dần
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "minXp"))
        );

        return mongoTemplate.aggregate(
                aggregation,
                "rank", // collection gốc
                RankResponse.class
        ).getMappedResults();
    }

    @Override
    public List<Rank> getAllRank() {
        return rankRepository.findAll();
    }

    @Override
    public Rank getRankById(ObjectId id) {
        return rankRepository.findById(id).orElseThrow(() -> new RuntimeException("Rank not found"));
    }

    @Override
    public Rank createRank(String name, int minXp, int maxXp, int rewardDiamond, String rewardCharacterId, MultipartFile badge) {
        Rank rank = new Rank();
        rank.setName(name);
        rank.setMinXp(minXp);
        rank.setMaxXp(maxXp);
        rank.setRewardDiamond(rewardDiamond);
        rank.setRewardCharacterId(new ObjectId(rewardCharacterId));
        try {
            String imageUrl = cloudinaryService.uploadFile(badge);
            rank.setBadge(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
        return rankRepository.save(rank);
    }

    @Override
    public Rank updateRank(ObjectId id, String name, int minXp, int maxXp, int rewardDiamond, String rewardCharacterId, MultipartFile badge) {
        Rank rank = rankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rank not found"));

        rank.setName(name);
        rank.setMinXp(minXp);
        rank.setMaxXp(maxXp);
        rank.setRewardDiamond(rewardDiamond);
        rank.setRewardCharacterId(new ObjectId(rewardCharacterId));

        if (badge != null && !badge.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadFile(badge);
                rank.setBadge(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        return rankRepository.save(rank);
    }

    @Override
    public void deleteRank(ObjectId id) {
        rankRepository.deleteById(id);
    }

    @Override
public RankResponse getRankByXp(int xp) {
            Rank rank = rankRepository.findAll().stream()
                    .filter(r -> xp >= r.getMinXp() && xp <= r.getMaxXp())
                    .findFirst()
                    .orElse(null);

            return rankMapping.toRankResponse(rank);
        }
}
