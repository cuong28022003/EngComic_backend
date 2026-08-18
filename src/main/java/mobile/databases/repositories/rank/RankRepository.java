package mobile.databases.repositories.rank;

import mobile.databases.entities.rank.RankEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("rankEntityRepository")
public interface RankRepository extends MongoRepository<RankEntity, String> {
    Optional<RankEntity> findByName(String name);
}
