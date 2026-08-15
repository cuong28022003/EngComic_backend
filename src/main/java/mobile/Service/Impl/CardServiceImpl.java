package mobile.Service.Impl;

import mobile.Service.CardService;
import mobile.config.SrsAlgorithm;
import mobile.model.Entity.Card;
import mobile.model.Entity.FamilyMember;
import mobile.model.Entity.WordFamily;
import mobile.repository.CardRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static mobile.constant.SrsConstant.DEFAULT_EASE_FACTOR;
import static mobile.constant.SrsConstant.FIRST_INTERVAL;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SrsAlgorithm srsAlgorithm;

    @Override
    public Page<Card> findByDeckId(ObjectId deckId, Pageable pageable) {
        return cardRepository.findByDeckId(deckId, pageable);
    }

    @Override
    public Page<Card> findDueCardsByDeckId(ObjectId deckId, Pageable pageable) {
        return cardRepository.findByDeckIdAndNextReviewLessThanEqual(deckId, new Date(), pageable);
    }

    @Override
    public Page<Card> findByDeckIdIn(List<ObjectId> deckIds, String search, Pageable pageable) {
        boolean hasSearch = (search != null && !search.trim().isEmpty());
        if (deckIds == null || deckIds.isEmpty()) {
            if (hasSearch) {
                return cardRepository.findByBackContainingIgnoreCaseOrFrontContainingIgnoreCase(search.trim(), search.trim(), pageable);
            }
            return cardRepository.findAll(pageable);
        }
        if (hasSearch) {
            return cardRepository.findByDeckIdInOrDeckIdNullAndSearch(deckIds, search.trim(), pageable);
        }
        return cardRepository.findByDeckIdInOrDeckIdNull(deckIds, pageable);
    }

    @Override
    public Card findById(ObjectId id) {
        return cardRepository.findById(id).orElse(null);
    }

    @Override
    public Card save(Card card) {
        Card savedCard = cardRepository.save(card);
        try {
            processAutoWordFamily(savedCard);
        } catch (Exception e) {
            System.err.println("Warning: processAutoWordFamily skipped due to: " + e.getMessage());
        }
        return savedCard;
    }

    private void processAutoWordFamily(Card primaryCard) {
        if (primaryCard == null || primaryCard.getDeckId() == null) return;
        WordFamily wf = primaryCard.getWordFamily();
        if (wf == null) {
            wf = new WordFamily();
            wf.setRootWord(primaryCard.getBack());
            primaryCard.setWordFamily(wf);
        }

        List<FamilyMember> members = wf.getMembers();
        if (members == null) {
            members = new ArrayList<>();
            wf.setMembers(members);
        }

        // Add compatibility fields to members if not present
        if (wf.getNoun() != null && !wf.getNoun().trim().isEmpty() && members.stream().noneMatch(m -> "noun".equalsIgnoreCase(m.getPartOfSpeech()))) {
            members.add(new FamilyMember(wf.getNoun().trim(), "noun", "", "", null));
        }
        if (wf.getVerb() != null && !wf.getVerb().trim().isEmpty() && members.stream().noneMatch(m -> "verb".equalsIgnoreCase(m.getPartOfSpeech()))) {
            members.add(new FamilyMember(wf.getVerb().trim(), "verb", "", "", null));
        }
        if (wf.getAdjective() != null && !wf.getAdjective().trim().isEmpty() && members.stream().noneMatch(m -> "adjective".equalsIgnoreCase(m.getPartOfSpeech()))) {
            members.add(new FamilyMember(wf.getAdjective().trim(), "adjective", "", "", null));
        }
        if (wf.getAdverb() != null && !wf.getAdverb().trim().isEmpty() && members.stream().noneMatch(m -> "adverb".equalsIgnoreCase(m.getPartOfSpeech()))) {
            members.add(new FamilyMember(wf.getAdverb().trim(), "adverb", "", "", null));
        }

        boolean updated = false;

        for (FamilyMember member : members) {
            if (member.getWord() == null || member.getWord().trim().isEmpty()) continue;
            String memberWord = member.getWord().trim();

            // Skip primary card itself
            if (memberWord.equalsIgnoreCase(primaryCard.getBack())) {
                member.setCardId(primaryCard.getId().toHexString());
                continue;
            }

            // Check if card already exists in the deck
            Optional<Card> existingOpt = cardRepository.findByDeckIdAndBackIgnoreCase(primaryCard.getDeckId(), memberWord);
            if (existingOpt.isPresent()) {
                Card existingCard = existingOpt.get();
                member.setCardId(existingCard.getId().toHexString());
                updated = true;
            } else {
                // Auto create new Card for missing family member!
                Card newCard = new Card();
                newCard.setDeckId(primaryCard.getDeckId());
                newCard.setBack(memberWord);
                newCard.setFront(primaryCard.getFront() + " [" + member.getPartOfSpeech() + "]");
                newCard.setPartOfSpeech(member.getPartOfSpeech());
                newCard.setTags(primaryCard.getTags());
                newCard.setLastReviewed(new Date());
                newCard.setNextReview(new Date());
                newCard.setInterval(FIRST_INTERVAL);
                newCard.setEaseFactor(DEFAULT_EASE_FACTOR);
                newCard.setRepetition(0);
                newCard.setLapses(0);

                Card savedAutoCard = cardRepository.save(newCard);
                member.setCardId(savedAutoCard.getId().toHexString());
                updated = true;
            }
        }

        if (updated) {
            cardRepository.save(primaryCard);
        }
    }

    @Override
    public void deleteById(ObjectId id) {
        cardRepository.deleteById(id);
    }

    @Override
    public Card review(ObjectId cardId, boolean isCorrect, String reviewState) {
        Card card = cardRepository.findById(cardId).orElse(null);
        if (card != null) {
            card = srsAlgorithm.updateCardReview(card, isCorrect, reviewState);
            return cardRepository.save(card);
        }
        return null;
    }

    @Override
    public List<Card> findAllByDeckId(ObjectId deckId) {
        return cardRepository.findByDeckId(deckId);
    }

    @Override
    public void deleteAllByDeckId(ObjectId deckId) {
        cardRepository.deleteAllByDeckId(deckId);
    }
}
