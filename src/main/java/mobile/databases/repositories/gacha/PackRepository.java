package mobile.databases.repositories.gacha;

import mobile.databases.entities.gacha.PackEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("packEntityRepository")
public interface PackRepository extends MongoRepository<PackEntity, String> {
    Optional<PackEntity> findByName(String name);
}

