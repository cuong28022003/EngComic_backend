package mobile.repository.comic;

import mobile.model.Entity.Comic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ComicRepositoryCustomImpl implements ComicRepositoryCustom{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Comic> searchComics(String keyword, String genre, String artist, ObjectId uploaderId, String sortBy, String sortDir, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            Criteria nameCriteria = Criteria.where("name").regex(keyword, "i");
            Criteria artistCriteria = Criteria.where("artist").regex(keyword, "i");
            criteriaList.add(new Criteria().orOperator(nameCriteria, artistCriteria));
        }

        if (genre != null && !genre.isBlank()) {
            criteriaList.add(Criteria.where("genre").is(genre));
        }

        if (artist != null && !artist.isBlank()) {
            criteriaList.add(Criteria.where("artist").regex(artist, "i"));
        }

        if (uploaderId != null) {
            criteriaList.add(Criteria.where("uploaderId").is(uploaderId));
        }

        criteriaList.add(Criteria.where("status").ne("LOCK"));

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // Sắp xếp
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        query.with(Sort.by(direction, sortBy));

        List<Comic> results = mongoTemplate.find(query, Comic.class);
        long total = mongoTemplate.count(query.skip(-1).limit(-1), Comic.class);

        return new PageImpl<>(results, pageable, total);
    }
}
