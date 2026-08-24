package mobile.apis.vocab.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardExercisePackageDto {

    private Level1RecognitionDto level1Recognition;
    private Level2ContextDto level2Context;
    private Level3ProductionDto level3Production;
    private Level4RealworldDto level4Realworld;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Level1RecognitionDto {
        private String question;
        @Builder.Default
        private List<ExerciseOptionDto> options = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Level2ContextDto {
        private String question;
        private String sentence;
        @Builder.Default
        private List<ExerciseOptionDto> options = new ArrayList<>();
        private String collocationNote;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Level3ProductionDto {
        private String prompt;
        @Builder.Default
        private List<String> shuffledWords = new ArrayList<>();
        private String correctSentence;
        private String vietnameseMeaning;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Level4RealworldDto {
        private String situation;
        private String sampleResponse;
        private String keyTakeaways;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExerciseOptionDto {
        private String text;
        @com.fasterxml.jackson.annotation.JsonProperty("isCorrect")
        private boolean isCorrect;

        @com.fasterxml.jackson.annotation.JsonProperty("is_correct")
        public boolean getIsCorrectSnake() {
            return isCorrect;
        }
    }
}
