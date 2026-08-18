package mobile.databases.repositories.usercharacter;

import mobile.databases.entities.usercharacter.UserCharacterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("userCharacterEntityRepository")
public interface UserCharacterRepository extends MongoRepository<UserCharacterEntity, String> {
    Page<UserCharacterEntity> findByUserId(String userId, Pageable pageable);
    List<UserCharacterEntity> findByUserId(String userId);
    Optional<UserCharacterEntity> findByUserIdAndCharacterId(String userId, String characterId);
}
