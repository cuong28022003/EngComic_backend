package mobile.apis.vocab.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PracticeQueueItemDto {
    private String id;
    private String word;
    private String meaning;
    private String ipa;
    private String audio;
    private String partOfSpeech;
    private String deckId;
    private int masteryLevel;
    private String status;
    private int wrongCount;
    private Date nextReview;
    private boolean hasExercisePackage;
    private CardExercisePackageDto exercisePackage;
}
