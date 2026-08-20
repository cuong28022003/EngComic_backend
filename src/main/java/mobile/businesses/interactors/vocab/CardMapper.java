package mobile.businesses.interactors.vocab;

import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.apis.vocab.dtos.CreateCardRequest;
import mobile.databases.entities.vocab.CardEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CardMapper {

    public CardResponseDto toResponse(CardEntity entity) {
        if (entity == null) return null;
        CardResponseDto response = new CardResponseDto();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setDeckId(entity.getDeckId());
        response.setWord(entity.getWord());
        response.setMeaning(entity.getMeaning());
        response.setIpa(entity.getIpa());
        response.setImage(entity.getImage());
        response.setAudio(entity.getAudio());
        response.setTags(entity.getTags());
        response.setPartOfSpeech(entity.getPartOfSpeech());
        response.setDefinitionEn(entity.getDefinitionEn());
        response.setUsageNote(entity.getUsageNote());
        response.setTopic(entity.getTopic());

        response.setExamples(entity.getExamples() != null ? entity.getExamples() : new ArrayList<>());
        response.setRelations(entity.getRelations() != null ? entity.getRelations() : new ArrayList<>());

        response.setPersonalNote(entity.getPersonalNote());
        response.setMyExample(entity.getMyExample());
        response.setFavorite(entity.isFavorite());

        response.setStage(entity.getStage());
        response.setStatus(entity.getStatus() != null ? entity.getStatus() : "new");
        response.setInterval(entity.getInterval());
        response.setEaseFactor(entity.getEaseFactor());
        response.setRepetition(entity.getRepetition());
        response.setLapses(entity.getLapses());
        response.setWrongCount(entity.getWrongCount());
        response.setReviewCount(entity.getReviewCount());

        if (entity.getLastReviewed() != null) {
            response.setLastReviewed(entity.getLastReviewed().toString());
        }
        if (entity.getNextReview() != null) {
            response.setNextReview(entity.getNextReview().toString());
        }
        if (entity.getCreateAt() != null) {
            response.setCreateAt(entity.getCreateAt().toString());
        }
        if (entity.getUpdateAt() != null) {
            response.setUpdateAt(entity.getUpdateAt().toString());
        }

        return response;
    }

    public CardEntity toEntity(CreateCardRequest request) {
        if (request == null) return null;
        CardEntity card = new CardEntity();
        card.setUserId(request.getUserId());
        card.setDeckId(request.getDeckId());
        card.setWord(request.getWord() != null ? request.getWord() : request.getFront());
        card.setMeaning(request.getMeaning() != null ? request.getMeaning() : request.getBack());
        card.setIpa(request.getIPA());
        card.setImage(request.getImage());
        card.setAudio(request.getAudio());
        card.setTags(request.getTags());
        card.setPartOfSpeech(request.getPartOfSpeech());
        card.setDefinitionEn(request.getDefinitionEn());
        card.setUsageNote(request.getUsageNote());
        card.setTopic(request.getTopic());

        if (request.getExamples() != null) {
            card.setExamples(request.getExamples());
        }
        if (request.getRelations() != null) {
            card.setRelations(request.getRelations());
        }

        card.setPersonalNote(request.getPersonalNote());
        card.setMyExample(request.getMyExample());
        card.setFavorite(request.isFavorite());

        card.setStage(0);
        card.setStatus("new");
        card.setInterval(0);
        card.setEaseFactor(2.5);
        card.setRepetition(0);
        card.setLapses(0);
        card.setWrongCount(0);
        card.setReviewCount(0);

        return card;
    }
}

