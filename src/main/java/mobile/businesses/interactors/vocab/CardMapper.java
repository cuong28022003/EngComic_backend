package mobile.businesses.interactors.vocab;

import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.apis.vocab.dtos.CreateCardRequest;
import mobile.databases.entities.vocab.CardEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

        response.setRelations(entity.getRelations() != null ? entity.getRelations() : new ArrayList<>());
        response.setUsages(entity.getUsages() != null ? entity.getUsages() : new ArrayList<>());
        response.setComparisonGroup(entity.getComparisonGroup());

        response.setPersonalNote(entity.getPersonalNote());
        response.setMyExample(entity.getMyExample());
        response.setFavorite(entity.isFavorite());

        response.setStage(entity.getStage());
        response.setMasteryLevel(entity.getMasteryLevel() > 0 ? entity.getMasteryLevel() : 1);
        response.setConfidenceScore(entity.getConfidenceScore());
        response.setMemoryTip(entity.getMemoryTip());

        boolean hasPkg = entity.getExercisePackage() != null;
        response.setHasExercisePackage(hasPkg);
        response.setExercisePackage(hasPkg ? toPkgDto(entity.getExercisePackage()) : null);

        response.setStatus(entity.getStatus() != null ? entity.getStatus() : "new");
        response.setInterval(entity.getInterval());
        response.setEaseFactor(entity.getEaseFactor());
        response.setRepetition(entity.getRepetition());
        response.setLapses(entity.getLapses());
        response.setWrongCount(entity.getWrongCount());
        response.setReviewCount(entity.getReviewCount());

        response.setLastReviewed(entity.getLastReviewed());
        response.setNextReview(entity.getNextReview());
        response.setCreateAt(entity.getCreateAt());
        response.setUpdateAt(entity.getUpdateAt());

        return response;
    }

    public mobile.apis.vocab.dtos.CardExercisePackageDto toPkgDto(mobile.databases.entities.vocab.CardExercisePackage pkg) {
        if (pkg == null) return null;
        mobile.apis.vocab.dtos.CardExercisePackageDto.CardExercisePackageDtoBuilder b = mobile.apis.vocab.dtos.CardExercisePackageDto.builder();

        if (pkg.getLevel1Recognition() != null) {
            List<mobile.apis.vocab.dtos.CardExercisePackageDto.ExerciseOptionDto> opts = new ArrayList<>();
            if (pkg.getLevel1Recognition().getOptions() != null) {
                for (mobile.databases.entities.vocab.CardExercisePackage.ExerciseOption o : pkg.getLevel1Recognition().getOptions()) {
                    opts.add(mobile.apis.vocab.dtos.CardExercisePackageDto.ExerciseOptionDto.builder()
                            .text(o.getText())
                            .isCorrect(o.isCorrect())
                            .build());
                }
            }
            b.level1Recognition(mobile.apis.vocab.dtos.CardExercisePackageDto.Level1RecognitionDto.builder()
                    .question(pkg.getLevel1Recognition().getQuestion())
                    .options(opts)
                    .build());
        }

        if (pkg.getLevel2Context() != null) {
            List<mobile.apis.vocab.dtos.CardExercisePackageDto.ExerciseOptionDto> opts = new ArrayList<>();
            if (pkg.getLevel2Context().getOptions() != null) {
                for (mobile.databases.entities.vocab.CardExercisePackage.ExerciseOption o : pkg.getLevel2Context().getOptions()) {
                    opts.add(mobile.apis.vocab.dtos.CardExercisePackageDto.ExerciseOptionDto.builder()
                            .text(o.getText())
                            .isCorrect(o.isCorrect())
                            .build());
                }
            }
            b.level2Context(mobile.apis.vocab.dtos.CardExercisePackageDto.Level2ContextDto.builder()
                    .question(pkg.getLevel2Context().getQuestion())
                    .sentence(pkg.getLevel2Context().getSentence())
                    .collocationNote(pkg.getLevel2Context().getCollocationNote())
                    .options(opts)
                    .build());
        }

        if (pkg.getLevel3Production() != null) {
            b.level3Production(mobile.apis.vocab.dtos.CardExercisePackageDto.Level3ProductionDto.builder()
                    .prompt(pkg.getLevel3Production().getPrompt())
                    .shuffledWords(pkg.getLevel3Production().getShuffledWords() != null ? pkg.getLevel3Production().getShuffledWords() : new ArrayList<>())
                    .correctSentence(pkg.getLevel3Production().getCorrectSentence())
                    .vietnameseMeaning(pkg.getLevel3Production().getVietnameseMeaning())
                    .build());
        }

        if (pkg.getLevel4Realworld() != null) {
            b.level4Realworld(mobile.apis.vocab.dtos.CardExercisePackageDto.Level4RealworldDto.builder()
                    .situation(pkg.getLevel4Realworld().getSituation())
                    .sampleResponse(pkg.getLevel4Realworld().getSampleResponse())
                    .keyTakeaways(pkg.getLevel4Realworld().getKeyTakeaways())
                    .build());
        }

        return b.build();
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

        if (request.getRelations() != null) {
            card.setRelations(request.getRelations());
        }
        if (request.getUsages() != null) {
            card.setUsages(request.getUsages());
        }
        card.setComparisonGroup(request.getComparisonGroup());

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

