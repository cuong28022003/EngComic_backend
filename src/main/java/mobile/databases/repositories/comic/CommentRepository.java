package mobile.databases.repositories.comic;

import mobile.databases.entities.comic.CommentEntity;
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

