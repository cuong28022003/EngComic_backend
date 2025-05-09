package mobile.Service.Impl;

import mobile.Service.GachaService;
import mobile.model.Entity.Character;
import mobile.model.Entity.Pack;
import mobile.model.payload.response.card.GachaPackResult;
import mobile.repository.CharacterRepository;
import mobile.repository.PackRepository;
import mobile.repository.UserCharacterCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GachaServiceImpl implements GachaService {
    @Autowired
    private PackRepository packRepository;

    @Autowired
    private CharacterRepository characterRepository;

    private final Random random = new Random();

    public List<GachaPackResult> roll(int count) {
        List<Pack> packs = packRepository.findAll();
        List<GachaPackResult> results = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Pack pack = packs.get(random.nextInt(packs.size()));
            List<Character> characters = characterRepository.findByPackId(pack.getId());

            if (characters.isEmpty()) continue;

            Character drawn = rollOneCard(characters);

            results.add(new GachaPackResult(
                    pack.getId().toHexString(),
                    pack.getName(),
                    pack.getCoverImageUrl(),
                    drawn
            ));
        }

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
}
