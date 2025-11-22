package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterService;
import mobile.Service.CharacterUsageService;
import mobile.Service.PackService;
import mobile.Service.UserCharacterService;
import mobile.mapping.CharacterMapping;
import mobile.mapping.UserCharacterMapping;
import mobile.model.Entity.Character;
import mobile.model.Entity.CharacterUsage;
import mobile.model.Entity.Pack;
import mobile.model.Entity.UserCharacter;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.model.payload.response.character.UserCharacterResponse;
import mobile.repository.character.CharacterRepository;
import mobile.repository.user_character.UserCharacterRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCharacterServiceImpl implements UserCharacterService {
    private final UserCharacterRepository userCharacterRepository;
    private final CharacterRepository characterRepository;
    private final PackService packService;
    private final CharacterUsageService characterUsageService;
    private final CharacterService characterService;
    private final UserCharacterMapping userCharacterMapping;
    private final CharacterMapping characterMapping;

    @Override
    public UserCharacter save(ObjectId userId, ObjectId characterId) {
        UserCharacter userCharacter = new UserCharacter();
        userCharacter.setUserId(userId);
        userCharacter.setCharacterId(characterId);
        userCharacter.setObtainedAt(LocalDateTime.now());
        userCharacterRepository.save(userCharacter);
        return userCharacter;
    }

    @Override
    public List<CharacterResponse> findAllByUserId(ObjectId userId) {
        List<UserCharacter> userCharacters = userCharacterRepository.findByUserId(userId);
        return userCharacters.stream()
                .map(userCharacter -> {
                    return characterService.findById(userCharacter.getCharacterId().toHexString());
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCharacterOwnedByUser(ObjectId userId, ObjectId id) {
        // Kiểm tra xem người dùng có sở hữu nhân vật với ID nhất định hay không
        return userCharacterRepository.existsByUserIdAndCharacterId(userId, id);
    }

    @Override
    public Page<CharacterResponse> searchUserCharacters(String name, String rarity, ObjectId userId, Pageable pageable) {
        Page<UserCharacter> userCharacters = userCharacterRepository.searchUserCharacters(name, rarity, userId, pageable);
        Page<CharacterResponse> userCharacterResponses = userCharacters.map(userCharacter -> {
            Character character = characterRepository.findById(userCharacter.getCharacterId()).orElse(null);
            return characterMapping.toCharacterResponse(character);
        });
        return userCharacterResponses;
    }
}
