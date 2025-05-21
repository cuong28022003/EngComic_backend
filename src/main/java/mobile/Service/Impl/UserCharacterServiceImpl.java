package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.PackService;
import mobile.Service.UserCharacterService;
import mobile.mapping.CharacterMapping;
import mobile.model.Entity.Character;
import mobile.model.Entity.Pack;
import mobile.model.Entity.UserCharacter;
import mobile.model.payload.response.character.UserCharacterResponse;
import mobile.repository.CharacterRepository;
import mobile.repository.UserCharacterRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCharacterServiceImpl implements UserCharacterService {
    private final UserCharacterRepository userCharacterRepository;
    private final CharacterRepository characterRepository;
    private final PackService packService;

    @Override
    public Page<UserCharacterResponse> getCharactersByUserId(ObjectId userId, String searchTerm, String rarity,
                                                             String sortBy, String sortDirection, Pageable pageable) {
        // Lấy danh sách UserCharacter từ repository
        List<UserCharacter> userCharacters = userCharacterRepository.findByUserId(userId);

        // Ánh xạ UserCharacter sang UserCharacterResponse
        List<UserCharacterResponse> userCharacterResponseList = userCharacters.stream()
                .map(userCharacter -> {
                    Character character = characterRepository.findById(userCharacter.getCharacterId()).orElse(null);
                    Pack pack = packService.getPackById(character.getPackId());
                    return CharacterMapping.toUserCharacterResponse(userCharacter, character, pack);
                })
                .filter(response -> {
                    // Tìm kiếm theo searchTerm (name hoặc packName)
                    if (searchTerm == null || searchTerm.isEmpty()) return true;
                    return (response.getName() != null && response.getName().toLowerCase().contains(searchTerm.toLowerCase())) ||
                            (response.getPack().getName() != null && response.getPack().getName().toLowerCase().contains(searchTerm.toLowerCase()));
                })
                .filter(response -> {
                    // Lọc theo rarity nếu được cung cấp
                    if (rarity == null || rarity.isEmpty()) return true;
                    return response.getRarity().equals(rarity);
                })
                .sorted((response1, response2) -> {
                    // Sắp xếp theo sortBy và sortDirection
                    int comparison = 0;
                    if ("name".equalsIgnoreCase(sortBy)) {
                        comparison = response1.getName().compareToIgnoreCase(response2.getName());
                    } else if ("obtainedAt".equalsIgnoreCase(sortBy)) {
                        comparison = response1.getObtainedAt().compareTo(response2.getObtainedAt());
                    }
                    return "desc".equalsIgnoreCase(sortDirection) ? -comparison : comparison;
                })
                .collect(Collectors.toList());

        // Áp dụng phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), userCharacterResponseList.size());
        List<UserCharacterResponse> paginatedList = userCharacterResponseList.subList(start, end);

        // Chuyển đổi danh sách sang Page
        return PageableExecutionUtils.getPage(paginatedList, pageable, userCharacterResponseList::size);
    }

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
    public List<UserCharacterResponse> findAllByUserId(ObjectId userId) {
        List<UserCharacter> userCharacters = userCharacterRepository.findByUserId(userId);
        return userCharacters.stream()
                .map(userCharacter -> {
                    Character character = characterRepository.findById(userCharacter.getCharacterId()).orElse(null);
                    Pack pack = packService.getPackById(character.getPackId());
                    return CharacterMapping.toUserCharacterResponse(userCharacter, character, pack);
                })
                .collect(Collectors.toList());
    }
}
