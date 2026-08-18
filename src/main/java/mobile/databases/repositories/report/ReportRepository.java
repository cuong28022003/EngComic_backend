package mobile.databases.repositories.report;

import mobile.databases.entities.report.ReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("reportEntityRepository")
public interface ReportRepository extends MongoRepository<ReportEntity, String> {
    Page<ReportEntity> findByStatus(String status, Pageable pageable);
    Page<ReportEntity> findByUserId(String userId, Pageable pageable);
}
