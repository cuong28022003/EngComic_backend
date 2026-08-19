package mobile.databases.entities.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.databases.entities.user.RankEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "user_stats")
public class UserStatsEntity {

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
    private boolean isReceivedSeasonReward;
    @Builder.Default
    private boolean isPremium = false;
    private LocalDateTime premiumExpiredAt;
}

