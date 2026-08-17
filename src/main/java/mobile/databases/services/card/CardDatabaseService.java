package mobile.databases.services.card;

import mobile.databases.entities.card.CardEntity;
import mobile.databases.entities.card.WordRelation;
import mobile.databases.repositories.card.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CardDatabaseService {

    @Autowired
    private CardRepository cardRepository;

    public void autoLinkRelations(String userId) {
        if (userId == null) return;
        List<CardEntity> allCards = cardRepository.findByUserId(userId);
        if (allCards.isEmpty()) return;

        Map<String, String> lookupMap = new HashMap<>();
        for (CardEntity c : allCards) {
            if (c.getFront() != null && c.getId() != null) {
                lookupMap.put(c.getFront().trim().toLowerCase(), c.getId());
            }
        }

        List<CardEntity> modifiedCards = new ArrayList<>();
        for (CardEntity card : allCards) {
            if (card.getRelations() == null || card.getRelations().isEmpty()) continue;
            boolean modified = false;
            for (WordRelation relation : card.getRelations()) {
                if (relation.getRelatedText() != null) {
                    String cleanRelated = relation.getRelatedText().trim().toLowerCase();
                    String matchedId = lookupMap.get(cleanRelated);
                    if (matchedId != null && !matchedId.equals(relation.getRelatedCardId())) {
                        relation.setRelatedCardId(matchedId);
                        modified = true;
                    }
                }
            }
            if (modified) {
                modifiedCards.add(card);
            }
        }

        if (!modifiedCards.isEmpty()) {
            cardRepository.saveAll(modifiedCards);
        }
    }
}
