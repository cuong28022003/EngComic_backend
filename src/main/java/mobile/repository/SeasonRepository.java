package mobile.repository;

import mobile.model.Entity.Season;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

@EnableMongoRepositories
public interface SeasonRepository extends MongoRepository<Season, ObjectId> {
    Optional<Season> findTopByOrderByEndDateDesc(); // lấy mùa mới nhất
}
