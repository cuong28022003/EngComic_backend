package mobile.Service.Impl;

import mobile.Service.CloudinaryService;
import mobile.Service.RankService;
import mobile.model.Entity.Rank;
import mobile.model.payload.request.rank.CreateRankRequest;
import mobile.repository.RankRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class RankServiceImpl implements RankService {
    private final RankRepository rankRepository;

    public RankServiceImpl(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public List<Rank> getAllRank() {
        return rankRepository.findAll();
    }

    @Override
    public Rank getRankById(ObjectId id) {
        return rankRepository.findById(id).orElseThrow(() -> new RuntimeException("Rank not found"));
    }

    @Override
    public Rank createRank(String name, int minXp, int maxXp, MultipartFile badge) {
        Rank rank = new Rank();
        rank.setName(name);
        rank.setMinXp(minXp);
        rank.setMaxXp(maxXp);
        try {
            String imageUrl = cloudinaryService.uploadFile(badge);
            rank.setBadge(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
        return rankRepository.save(rank);
    }

    @Override
    public Rank updateRank(ObjectId id, String name, int minXp, int maxXp, MultipartFile badge) {
        Rank rank = rankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rank not found"));

        rank.setName(name);
        rank.setMinXp(minXp);
        rank.setMaxXp(maxXp);

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
    public Rank getRankByXp(int xp) {
        return rankRepository.findAll().stream()
                .filter(rank -> xp >= rank.getMinXp() && xp <= rank.getMaxXp())
                .findFirst()
                .orElse(null);
    }
}
