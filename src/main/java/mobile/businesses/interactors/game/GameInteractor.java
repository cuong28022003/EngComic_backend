package mobile.businesses.interactors.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.businesses.boundaries.game.*;
import mobile.databases.entities.gacha.CharacterEntity;
import mobile.databases.entities.game.*;
import mobile.databases.repositories.gacha.CharacterRepository;
import mobile.databases.repositories.game.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameInteractor implements
        GetCharacterSpritesBoundary,
        SaveCharacterSpritesBoundary,
        GetCharacterAnimationsBoundary,
        SaveCharacterAnimationsBoundary,
        GetCharacterDataBoundary,
        SaveCharacterDataBoundary,
        GetCharacterStatsBoundary,
        SaveCharacterStatsBoundary,
        GetCharacterSoundsBoundary,
        SaveCharacterSoundsBoundary,
        GetGameCharacterBoundary {

    private final CharacterSpriteRepository spriteRepository;
    private final CharacterAnimationRepository animationRepository;
    private final CharacterDataRepository dataRepository;
    private final CharacterStatsRepository statsRepository;
    private final CharacterSoundRepository soundRepository;
    private final CharacterRepository characterRepository;

    @Override
    public GetCharacterSpritesBoundary.Response execute(GetCharacterSpritesBoundary.Request request) {
        String characterId = request.getCharacterId();
        CharacterSpriteEntity entity = spriteRepository.findByCharacterId(characterId)
                .orElseGet(() -> CharacterSpriteEntity.builder()
                        .characterId(characterId)
                        .groups(new HashMap<>())
                        .build());
        return GetCharacterSpritesBoundary.Response.builder().data(entity).build();
    }

    @Override
    public SaveCharacterSpritesBoundary.Response execute(SaveCharacterSpritesBoundary.Request request) {
        CharacterSpriteEntity entity = request.getEntity();
        if (entity != null && entity.getCharacterId() != null) {
            spriteRepository.findByCharacterId(entity.getCharacterId()).ifPresent(existing -> {
                entity.setId(existing.getId());
            });
            CharacterSpriteEntity saved = spriteRepository.save(entity);
            return SaveCharacterSpritesBoundary.Response.builder().data(saved).build();
        }
        return SaveCharacterSpritesBoundary.Response.builder().build();
    }

    @Override
    public GetCharacterAnimationsBoundary.Response execute(GetCharacterAnimationsBoundary.Request request) {
        String characterId = request.getCharacterId();
        CharacterAnimationEntity entity = animationRepository.findByCharacterId(characterId)
                .orElseGet(() -> CharacterAnimationEntity.builder()
                        .characterId(characterId)
                        .actions(new HashMap<>())
                        .build());
        return GetCharacterAnimationsBoundary.Response.builder().data(entity).build();
    }

    @Override
    public SaveCharacterAnimationsBoundary.Response execute(SaveCharacterAnimationsBoundary.Request request) {
        CharacterAnimationEntity entity = request.getEntity();
        if (entity != null && entity.getCharacterId() != null) {
            animationRepository.findByCharacterId(entity.getCharacterId()).ifPresent(existing -> {
                entity.setId(existing.getId());
            });
            CharacterAnimationEntity saved = animationRepository.save(entity);
            return SaveCharacterAnimationsBoundary.Response.builder().data(saved).build();
        }
        return SaveCharacterAnimationsBoundary.Response.builder().build();
    }

    @Override
    public GetCharacterDataBoundary.Response execute(GetCharacterDataBoundary.Request request) {
        String characterId = request.getCharacterId();
        CharacterDataEntity entity = dataRepository.findByCharacterId(characterId)
                .orElseGet(() -> CharacterDataEntity.builder()
                        .characterId(characterId)
                        .scale(1.0)
                        .data(new HashMap<>())
                        .build());
        return GetCharacterDataBoundary.Response.builder().data(entity).build();
    }

    @Override
    public SaveCharacterDataBoundary.Response execute(SaveCharacterDataBoundary.Request request) {
        CharacterDataEntity entity = request.getEntity();
        if (entity != null && entity.getCharacterId() != null) {
            dataRepository.findByCharacterId(entity.getCharacterId()).ifPresent(existing -> {
                entity.setId(existing.getId());
            });
            CharacterDataEntity saved = dataRepository.save(entity);
            return SaveCharacterDataBoundary.Response.builder().data(saved).build();
        }
        return SaveCharacterDataBoundary.Response.builder().build();
    }

    @Override
    public GetCharacterStatsBoundary.Response execute(GetCharacterStatsBoundary.Request request) {
        String characterId = request.getCharacterId();
        CharacterStatsEntity entity = statsRepository.findByCharacterId(characterId)
                .orElseGet(() -> {
                    Map<String, Object> defaultStates = new HashMap<>();
                    defaultStates.put("idle", Map.of("name", "idle", "animationNumber", 0));
                    defaultStates.put("run", Map.of("name", "run", "animationNumber", 20));
                    return CharacterStatsEntity.builder()
                            .characterId(characterId)
                            .states(defaultStates)
                            .build();
                });
        return GetCharacterStatsBoundary.Response.builder().data(entity).build();
    }

    @Override
    public SaveCharacterStatsBoundary.Response execute(SaveCharacterStatsBoundary.Request request) {
        CharacterStatsEntity entity = request.getEntity();
        if (entity != null && entity.getCharacterId() != null) {
            statsRepository.findByCharacterId(entity.getCharacterId()).ifPresent(existing -> {
                entity.setId(existing.getId());
            });
            CharacterStatsEntity saved = statsRepository.save(entity);
            return SaveCharacterStatsBoundary.Response.builder().data(saved).build();
        }
        return SaveCharacterStatsBoundary.Response.builder().build();
    }

    @Override
    public GetCharacterSoundsBoundary.Response execute(GetCharacterSoundsBoundary.Request request) {
        String characterId = request.getCharacterId();
        CharacterSoundEntity entity = soundRepository.findByCharacterId(characterId)
                .orElseGet(() -> CharacterSoundEntity.builder()
                        .characterId(characterId)
                        .sounds(new HashMap<>())
                        .build());
        return GetCharacterSoundsBoundary.Response.builder().data(entity).build();
    }

    @Override
    public SaveCharacterSoundsBoundary.Response execute(SaveCharacterSoundsBoundary.Request request) {
        CharacterSoundEntity entity = request.getEntity();
        if (entity != null && entity.getCharacterId() != null) {
            soundRepository.findByCharacterId(entity.getCharacterId()).ifPresent(existing -> {
                entity.setId(existing.getId());
            });
            CharacterSoundEntity saved = soundRepository.save(entity);
            return SaveCharacterSoundsBoundary.Response.builder().data(saved).build();
        }
        return SaveCharacterSoundsBoundary.Response.builder().build();
    }

    @Override
    public GetGameCharacterBoundary.Response execute(GetGameCharacterBoundary.Request request) {
        String characterId = request.getCharacterId();
        CharacterEntity entity = null;
        try {
            entity = characterRepository.findById(characterId).orElse(null);
        } catch (Exception e) {
            log.warn("Error finding character by id {}: {}", characterId, e.getMessage());
        }
        if (entity == null) {
            entity = CharacterEntity.builder()
                    .id(characterId)
                    .name(characterId)
                    .rarity("SSR")
                    .build();
        }
        return GetGameCharacterBoundary.Response.builder().data(entity).build();
    }
}
