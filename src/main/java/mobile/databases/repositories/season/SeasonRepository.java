package mobile.databases.repositories.season;

import mobile.databases.entities.season.SeasonEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("seasonEntityRepository")
public interface SeasonRepository extends MongoRepository<SeasonEntity, String> {
    Optional<SeasonEntity> findByIsActiveTrue();
}
