package mobile.repository.user_character;

import mobile.model.Entity.UserCharacter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserCharacterRepositoryCustomImpl implements UserCharacterRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<UserCharacter> searchUserCharacters(String name, String rarity, ObjectId userId, Pageable pageable) {
        List<AggregationOperation> operations = new ArrayList<>();

        // Match userId
        Criteria criteria = Criteria.where("userId").is(userId);
        operations.add(Aggregation.match(criteria));

        // Lookup character
        operations.add(Aggregation.lookup("character", "characterId", "_id", "character"));

        // Unwind character
        operations.add(Aggregation.unwind("character"));

        // Filter theo tên và rarity nếu có
        List<Criteria> characterCriteria = new ArrayList<>();
        if (name != null && !name.isBlank()) {
            characterCriteria.add(Criteria.where("character.name").regex(name, "i"));
        }
        if (rarity != null && !rarity.isBlank()) {
            characterCriteria.add(Criteria.where("character.rarity").is(rarity));
        }
        if (!characterCriteria.isEmpty()) {
            operations.add(Aggregation.match(new Criteria().andOperator(characterCriteria.toArray(new Criteria[0]))));
        }

        // Sort
        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "obtainedAt")));

        // Pagination
        long total = mongoTemplate.aggregate(Aggregation.newAggregation(operations), "user_character", UserCharacter.class).getMappedResults().size();

        operations.add(Aggregation.skip(pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        // Execute
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<UserCharacter> results = mongoTemplate.aggregate(aggregation, "user_character", UserCharacter.class).getMappedResults();

        return new PageImpl<>(results, pageable, total);
    }
}
