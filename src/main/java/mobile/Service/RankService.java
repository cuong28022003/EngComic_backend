package mobile.Service;

import mobile.model.Entity.Rank;
import mobile.model.payload.request.rank.CreateRankRequest;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RankService {
    List<Rank> getAllRanks();
    Rank getRankById(ObjectId id);
    Rank createRank(String name, int minXp, int maxXp, MultipartFile badge);
    Rank updateRank(ObjectId id, String name, int minXp, int maxXp, MultipartFile badge);
    void deleteRank(ObjectId id);
    Rank getRankByXp(int xp);
}
