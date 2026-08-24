package mobile.databases.entities.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardExercisePackage {

    private Level1Recognition level1Recognition;
    private Level2Context level2Context;
    private Level3Production level3Production;
    private Level4Realworld level4Realworld;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Level1Recognition {
        private String question;
        @Builder.Default
        private List<ExerciseOption> options = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Level2Context {
        private String question;
        private String sentence;
        @Builder.Default
        private List<ExerciseOption> options = new ArrayList<>();
        private String collocationNote;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Level3Production {
        private String prompt;
        @Builder.Default
        private List<String> shuffledWords = new ArrayList<>();
        private String correctSentence;
        private String vietnameseMeaning;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Level4Realworld {
        private String situation;
        private String sampleResponse;
        private String keyTakeaways;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExerciseOption {
        private String text;
        private boolean isCorrect;
    }
}
