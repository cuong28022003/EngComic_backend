package mobile.databases.repositories.comment;

import mobile.databases.entities.comment.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("commentEntityRepository")
public interface CommentRepository extends MongoRepository<CommentEntity, String> {
    Page<CommentEntity> findByComicUrlAndDepth(String comicUrl, int depth, Pageable pageable);
    List<CommentEntity> findByParentId(String parentId);
}
