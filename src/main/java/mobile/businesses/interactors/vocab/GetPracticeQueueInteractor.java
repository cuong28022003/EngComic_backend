package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CardExercisePackageDto;
import mobile.apis.vocab.dtos.PracticeQueueItemDto;
import mobile.businesses.boundaries.vocab.GetPracticeQueueBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.entities.vocab.CardExercisePackage;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GetPracticeQueueInteractor implements GetPracticeQueueBoundary {

    private final CardRepository cardRepository;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        String deckId = request.getDeckId();
        int limit = request.getLimit() > 0 ? Math.min(request.getLimit(), 30) : 20;

        Date now = new Date();
        List<CardEntity> rawCards;

        if (deckId != null && !deckId.isBlank()) {
            rawCards = cardRepository.findByUserIdAndDeckId(userId, deckId);
        } else {
            // Get due cards (learning/mature due today) + new cards
            List<CardEntity> dueCards = cardRepository.findDueCards(userId, now, PageRequest.of(0, limit));
            int remaining = limit - dueCards.size();
            List<CardEntity> newCards = remaining > 0
                    ? cardRepository.findNewCards(userId, PageRequest.of(0, remaining))
                    : Collections.emptyList();

            Set<String> seenIds = new HashSet<>();
            rawCards = new ArrayList<>();
            for (CardEntity c : dueCards) {
                if (c.getId() != null && seenIds.add(c.getId())) {
                    rawCards.add(c);
                }
            }
            for (CardEntity c : newCards) {
                if (c.getId() != null && seenIds.add(c.getId())) {
                    rawCards.add(c);
                }
            }
        }

        List<PracticeQueueItemDto> items = new ArrayList<>();
        int totalDueCount = 0;

        for (CardEntity c : rawCards) {
            // Exclude leech cards and cards that do NOT have exercisePackage from practice queue
            if ("leech".equalsIgnoreCase(c.getStatus()) || c.getExercisePackage() == null) {
                continue;
            }

            totalDueCount++;
            if (items.size() < limit) {
                items.add(toQueueDto(c));
            }
        }

        return Response.builder()
                .items(items)
                .totalDue(totalDueCount)
                .build();
    }

    private PracticeQueueItemDto toQueueDto(CardEntity c) {
        CardExercisePackage pkg = c.getExercisePackage();
        boolean hasPkg = pkg != null;
        CardExercisePackageDto pkgDto = hasPkg ? toPkgDto(pkg) : null;

        return PracticeQueueItemDto.builder()
                .id(c.getId())
                .word(c.getWord())
                .meaning(c.getMeaning())
                .ipa(c.getIpa())
                .audio(c.getAudio())
                .partOfSpeech(c.getPartOfSpeech())
                .deckId(c.getDeckId())
                .masteryLevel(c.getMasteryLevel() > 0 ? c.getMasteryLevel() : 1)
                .status(c.getStatus() != null ? c.getStatus() : "new")
                .wrongCount(c.getWrongCount())
                .nextReview(c.getNextReview())
                .hasExercisePackage(hasPkg)
                .exercisePackage(pkgDto)
                .build();
    }

    private CardExercisePackageDto toPkgDto(CardExercisePackage pkg) {
        CardExercisePackageDto.CardExercisePackageDtoBuilder b = CardExercisePackageDto.builder();

        if (pkg.getLevel1Recognition() != null) {
            List<CardExercisePackageDto.ExerciseOptionDto> opts = new ArrayList<>();
            if (pkg.getLevel1Recognition().getOptions() != null) {
                for (CardExercisePackage.ExerciseOption o : pkg.getLevel1Recognition().getOptions()) {
                    opts.add(CardExercisePackageDto.ExerciseOptionDto.builder()
                            .text(o.getText())
                            .isCorrect(o.isCorrect())
                            .build());
                }
            }
            b.level1Recognition(CardExercisePackageDto.Level1RecognitionDto.builder()
                    .question(pkg.getLevel1Recognition().getQuestion())
                    .options(opts)
                    .build());
        }

        if (pkg.getLevel2Context() != null) {
            List<CardExercisePackageDto.ExerciseOptionDto> opts = new ArrayList<>();
            if (pkg.getLevel2Context().getOptions() != null) {
                for (CardExercisePackage.ExerciseOption o : pkg.getLevel2Context().getOptions()) {
                    opts.add(CardExercisePackageDto.ExerciseOptionDto.builder()
                            .text(o.getText())
                            .isCorrect(o.isCorrect())
                            .build());
                }
            }
            b.level2Context(CardExercisePackageDto.Level2ContextDto.builder()
                    .question(pkg.getLevel2Context().getQuestion())
                    .sentence(pkg.getLevel2Context().getSentence())
                    .collocationNote(pkg.getLevel2Context().getCollocationNote())
                    .options(opts)
                    .build());
        }

        if (pkg.getLevel3Production() != null) {
            b.level3Production(CardExercisePackageDto.Level3ProductionDto.builder()
                    .prompt(pkg.getLevel3Production().getPrompt())
                    .shuffledWords(pkg.getLevel3Production().getShuffledWords())
                    .correctSentence(pkg.getLevel3Production().getCorrectSentence())
                    .vietnameseMeaning(pkg.getLevel3Production().getVietnameseMeaning())
                    .build());
        }

        if (pkg.getLevel4Realworld() != null) {
            b.level4Realworld(CardExercisePackageDto.Level4RealworldDto.builder()
                    .situation(pkg.getLevel4Realworld().getSituation())
                    .sampleResponse(pkg.getLevel4Realworld().getSampleResponse())
                    .keyTakeaways(pkg.getLevel4Realworld().getKeyTakeaways())
                    .build());
        }

        return b.build();
    }
}
