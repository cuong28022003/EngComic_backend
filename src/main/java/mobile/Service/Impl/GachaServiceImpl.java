package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.GachaService;
import mobile.Service.UserCharacterService;
import mobile.mapping.CharacterMapping;
import mobile.model.Entity.Character;
import mobile.model.Entity.Pack;
import mobile.model.Entity.UserStats;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.model.payload.response.pack.GachaPackResult;
import mobile.repository.CharacterRepository;
import mobile.repository.PackRepository;
import mobile.repository.UserStatsRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GachaServiceImpl implements GachaService {
    @Autowired
    private PackRepository packRepository;

    @Autowired
    UserCharacterService userCharacterService;

    @Autowired
    private CharacterRepository characterRepository;
    private final CharacterMapping characterMapping;

    private final UserStatsRepository userStatsRepository;

    private final Random random = new Random();

    public List<CharacterResponse> roll(ObjectId userId, int count) {
        UserStats userStats = userStatsRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User stats not found"));

        int totalCost = count * 100;
        if (userStats.getDiamond() < totalCost) {
            throw new RuntimeException("Not enough diamonds");
        }

        int diamondReward = 0;

        List<Pack> packs = packRepository.findAll();
        List<CharacterResponse> results = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Pack pack = packs.get(random.nextInt(packs.size()));
            List<Character> characters = characterRepository.findByPackId(pack.getId());

            if (characters.isEmpty()) continue;

            Character drawn = rollOneCard(characters);

            boolean isOwned = userCharacterService.isCharacterOwnedByUser(userId, drawn.getId());

            if (isOwned) {
                // Quy đổi kim cương dựa trên độ hiếm nếu bị trùng
                diamondReward += getDiamondRewardByRarity(drawn.getRarity());
            } else {
                // Lưu thẻ mới vào danh sách sở hữu của người dùng
                userCharacterService.save(userId, drawn.getId());
            }
            results.add(characterMapping.toCharacterResponse(drawn));
        }

        // Trừ diamond
        userStats.setDiamond(userStats.getDiamond() - totalCost + diamondReward);
        userStatsRepository.save(userStats);

//        results.forEach(result -> {
//            userCharacterService.save(userId, result.getId());
//        });

        return results;
    }

    private Character rollOneCard(List<Character> pool) {
        double roll = Math.random();
        String rarity;
        if (roll < 0.01) rarity = "SSR";
        else if (roll < 0.10) rarity = "SR";
        else if (roll < 0.35) rarity = "R";
        else rarity = "C";

        List<Character> filtered = pool.stream()
                .filter(c -> c.getRarity().equals(rarity))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) filtered = pool;

        return filtered.get(random.nextInt(filtered.size()));
    }

    private int getDiamondRewardByRarity(String rarity) {
        switch (rarity) {
            case "SSR": return 100;
            case "SR": return 50;
            case "R": return 20;
            case "C": return 10;
            default: return 0;
        }
    }
}
