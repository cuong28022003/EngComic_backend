package mobile.databases.entities.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "user_learning_stats")
public class UserLearningStatsEntity {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;

    @Builder.Default
    private int xp = 0;

    @Builder.Default
    private int diamond = 0;

    private RankEntity rank;

    @Builder.Default
    private int currentStreak = 0;

    @Builder.Default
    private int longestStreak = 0;

    @Builder.Default
    private LocalDate lastStudyDate = LocalDate.now().minusDays(1);

    @Builder.Default
    private int totalWordsMastered = 0;

    @Builder.Default
    private int totalSessionsCompleted = 0;

    private String equippedTitle;
    private String equippedAvatarFrame;

    @Builder.Default
    private List<String> unlockedTitles = new ArrayList<>();

    @Builder.Default
    private List<String> unlockedAvatarFrames = new ArrayList<>();

    @Builder.Default
    private List<String> claimedAchievementIds = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
