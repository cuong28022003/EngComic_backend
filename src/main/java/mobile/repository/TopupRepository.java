package mobile.repository;

import mobile.model.Entity.Topup;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
public interface TopupRepository extends MongoRepository<Topup, ObjectId> {
    Page<Topup> findAll(Pageable pageable);
    List<Topup> findByProcessed(boolean processed);
    List<Topup> findByUserIdOrderByCreatedAtDesc(ObjectId userId);
}
