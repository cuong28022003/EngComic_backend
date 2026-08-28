package mobile.databases.repositories.grammar;

import mobile.databases.entities.grammar.GrammarPointEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrammarPointRepository extends MongoRepository<GrammarPointEntity, String> {

    Optional<GrammarPointEntity> findByTopicIgnoreCase(String topic);

    List<GrammarPointEntity> findByCategoryIgnoreCaseOrderByTopicAsc(String category);

    @Query("{ '$or': [ " +
            "{ 'topic': { $regex: ?0, $options: 'i' } }, " +
            "{ 'summary': { $regex: ?0, $options: 'i' } }, " +
            "{ 'signalWords': { $regex: ?0, $options: 'i' } }, " +
            "{ 'searchKeywords': { $regex: ?0, $options: 'i' } }, " +
            "{ 'category': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<GrammarPointEntity> searchGrammarPoints(String keyword);
}
