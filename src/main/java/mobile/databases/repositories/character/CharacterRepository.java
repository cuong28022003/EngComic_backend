package mobile.databases.repositories.character;

import mobile.databases.entities.character.CharacterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("characterEntityRepository")
public interface CharacterRepository extends MongoRepository<CharacterEntity, String> {
    Page<CharacterEntity> findByPackId(String packId, Pageable pageable);
    List<CharacterEntity> findByPackId(String packId);
    List<CharacterEntity> findByRarity(String rarity);
}
