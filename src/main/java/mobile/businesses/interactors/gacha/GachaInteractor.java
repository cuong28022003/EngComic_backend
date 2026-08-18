package mobile.businesses.interactors.gacha;

import lombok.RequiredArgsConstructor;
import mobile.apis.gacha.dtos.CharacterResponseDto;
import mobile.apis.gacha.dtos.PackResponseDto;
import mobile.apis.gacha.dtos.UserCharacterResponseDto;
import mobile.businesses.boundaries.gacha.GetCharacters;
import mobile.businesses.boundaries.gacha.GetPacks;
import mobile.businesses.boundaries.gacha.GetUserCharacters;
import mobile.businesses.boundaries.gacha.RollGacha;
import mobile.databases.entities.character.CharacterEntity;
import mobile.databases.entities.pack.PackEntity;
import mobile.databases.entities.usercharacter.UserCharacterEntity;
import mobile.databases.repositories.character.CharacterRepository;
import mobile.databases.repositories.pack.PackRepository;
import mobile.databases.repositories.usercharacter.UserCharacterRepository;
import mobile.domains.gacha.GachaRules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GachaInteractor implements GetCharacters, GetPacks, GetUserCharacters, RollGacha {

    private final CharacterRepository characterRepository;
    private final PackRepository packRepository;
    private final UserCharacterRepository userCharacterRepository;
    private final Random random = new Random();

    @Override
    public GetCharacters.Response execute(GetCharacters.Request request) {
        Page<CharacterEntity> page;
        if (request.getPackId() != null) {
            page = characterRepository.findByPackId(request.getPackId(), request.getPageable());
        } else {
            page = characterRepository.findAll(request.getPageable());
        }

        Page<CharacterResponseDto> dtoPage = page.map(this::mapCharacter);
        return GetCharacters.Response.builder()
                .characters(dtoPage)
                .build();
    }

    @Override
    public GetPacks.Response execute(GetPacks.Request request) {
        List<PackEntity> packs = packRepository.findAll();
        List<PackResponseDto> dtos = packs.stream().map(pack -> PackResponseDto.builder()
                .id(pack.getId())
                .name(pack.getName())
                .imageUrl(pack.getImageUrl())
                .description(pack.getDescription())
                .build()).collect(Collectors.toList());

        return GetPacks.Response.builder()
                .packs(dtos)
                .build();
    }

    @Override
    public GetUserCharacters.Response execute(GetUserCharacters.Request request) {
        Page<UserCharacterEntity> page = userCharacterRepository.findByUserId(request.getUserId(), request.getPageable());

        List<UserCharacterResponseDto> dtos = page.getContent().stream().map(uc -> {
            CharacterResponseDto charDto = null;
            if (uc.getCharacterId() != null) {
                charDto = characterRepository.findById(uc.getCharacterId()).map(this::mapCharacter).orElse(null);
            }
            return UserCharacterResponseDto.builder()
                    .id(uc.getId())
                    .userId(uc.getUserId())
                    .characterId(uc.getCharacterId())
                    .obtainedAt(uc.getObtainedAt())
                    .character(charDto)
                    .build();
        }).collect(Collectors.toList());

        return GetUserCharacters.Response.builder()
                .userCharacters(new PageImpl<>(dtos, request.getPageable(), page.getTotalElements()))
                .build();
    }

    @Override
    public RollGacha.Response execute(RollGacha.Request request) {
        String rarity = GachaRules.rollRarity();
        List<CharacterEntity> pool = characterRepository.findByRarity(rarity);
        if (pool.isEmpty()) {
            pool = characterRepository.findAll();
        }

        if (pool.isEmpty()) {
            throw new IllegalStateException("No characters available for gacha");
        }

        CharacterEntity selected = pool.get(random.nextInt(pool.size()));
        boolean isNew = userCharacterRepository.findByUserIdAndCharacterId(request.getUserId(), selected.getId()).isEmpty();

        if (isNew) {
            userCharacterRepository.save(UserCharacterEntity.builder()
                    .userId(request.getUserId())
                    .characterId(selected.getId())
                    .obtainedAt(LocalDateTime.now())
                    .build());
        }

        return RollGacha.Response.builder()
                .character(mapCharacter(selected))
                .isNew(isNew)
                .build();
    }

    private CharacterResponseDto mapCharacter(CharacterEntity entity) {
        if (entity == null) return null;
        return CharacterResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .rarity(entity.getRarity())
                .imageUrl(entity.getImageUrl())
                .description(entity.getDescription())
                .packId(entity.getPackId())
                .bonusXp(entity.getBonusXp())
                .bonusDiamond(entity.getBonusDiamond())
                .skillsUsagePerDay(entity.getSkillsUsagePerDay())
                .version(entity.getVersion())
                .type(entity.getType())
                .transformationCharacterId(entity.getTransformationCharacterId())
                .build();
    }
}
