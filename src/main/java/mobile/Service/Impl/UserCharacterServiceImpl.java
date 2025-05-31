package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterService;
import mobile.Service.CharacterUsageService;
import mobile.Service.PackService;
import mobile.Service.UserCharacterService;
import mobile.mapping.CharacterMapping;
import mobile.model.Entity.Character;
import mobile.model.Entity.CharacterUsage;
import mobile.model.Entity.Pack;
import mobile.model.Entity.UserCharacter;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.model.payload.response.character.UserCharacterResponse;
import mobile.repository.CharacterRepository;
import mobile.repository.UserCharacterRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
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
                    CharacterUsage characterUsage = characterUsageService.getOrCreateUsage(userId, character.getId(), LocalDate.now());
                    return CharacterMapping.toUserCharacterResponse(userCharacter, character, pack, characterUsage);
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
//                .sorted((response1, response2) -> {
//                    // Sắp xếp theo sortBy và sortDirection
//                    int comparison = 0;
//                    if ("name".equalsIgnoreCase(sortBy)) {
//                        comparison = response1.getName().compareToIgnoreCase(response2.getName());
//                    } else if ("obtainedAt".equalsIgnoreCase(sortBy)) {
//                        comparison = response1.getObtainedAt().compareTo(response2.getObtainedAt());
//                    }
//                    return "desc".equalsIgnoreCase(sortDirection) ? -comparison : comparison;
//                })
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
    public List<CharacterResponse> findAllByUserId(ObjectId userId) {
        List<UserCharacter> userCharacters = userCharacterRepository.findByUserId(userId);
        return userCharacters.stream()
                .map(userCharacter -> {
                    return characterService.findById(userCharacter.getCharacterId());
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCharacterOwnedByUser(ObjectId userId, ObjectId id) {
        // Kiểm tra xem người dùng có sở hữu nhân vật với ID nhất định hay không
        return userCharacterRepository.existsByUserIdAndCharacterId(userId, id);
    }

    private List<UserCharacterResponse> searchCharactersAndPacks(List<UserCharacterResponse> characters, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) return characters;

        String lowerSearchTerm = searchTerm.toLowerCase();
        return characters.stream()
                .filter(response ->
                        (response.getName() != null && response.getName().toLowerCase().contains(lowerSearchTerm)) ||
                                (response.getPack().getName() != null && response.getPack().getName().toLowerCase().contains(lowerSearchTerm))
                )
                .collect(Collectors.toList());
    }

    private List<UserCharacterResponse> filterCharactersByRarity(List<UserCharacterResponse> characters, String rarity) {
        if (rarity == null || rarity.isEmpty()) return characters;

        return characters.stream()
                .filter(response -> rarity.equalsIgnoreCase(response.getRarity()))
                .collect(Collectors.toList());
    }

    private List<UserCharacterResponse> sortCharacters(List<UserCharacterResponse> characters, String sortBy, String sortDirection) {
        Comparator<UserCharacterResponse> comparator;

        if ("name".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(UserCharacterResponse::getName, String.CASE_INSENSITIVE_ORDER);
        } else if ("obtainedAt".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(UserCharacterResponse::getObtainedAt);
        } else {
            // Mặc định không sắp xếp
            return characters;
        }

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return characters.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }


}
