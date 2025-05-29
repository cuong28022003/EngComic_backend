package mobile.repository;

import mobile.model.Entity.Report;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends MongoRepository<Report, ObjectId> {
    Page<Report> findByComicId(ObjectId comicId, Pageable pageable);

    List<Report> findAllByComicId(ObjectId comicId);

    Page<Report> findByStatus(String status, Pageable pageable);
}