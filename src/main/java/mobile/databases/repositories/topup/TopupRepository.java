package mobile.databases.repositories.topup;

import mobile.databases.entities.topup.TopupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("topupEntityRepository")
public interface TopupRepository extends MongoRepository<TopupEntity, String> {
    Page<TopupEntity> findByUserId(String userId, Pageable pageable);
    Page<TopupEntity> findByProcessed(boolean processed, Pageable pageable);
}
