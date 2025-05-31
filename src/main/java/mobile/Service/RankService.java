package mobile.Service;

import mobile.model.Entity.Rank;
import mobile.model.payload.request.rank.CreateRankRequest;
import mobile.model.payload.response.rank.RankResponse;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RankService {

    List<Rank> getAllRank();

    List<RankResponse> getAllRankWithCharacterAndPack();

    Rank getRankById(ObjectId id);

    Rank createRank(String name, int minXp, int maxXp, int rewardDiamond, String rewardCharacterId, MultipartFile badge);

    Rank updateRank(ObjectId id, String name, int minXp, int maxXp, int rewardDiamond, String rewardCharacterId, MultipartFile badge);

    void deleteRank(ObjectId id);

    RankResponse getRankByXp(int xp);
}
