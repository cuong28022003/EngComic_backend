package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Date;

@Document(collection = "user_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStats {
    @Id
    private ObjectId id;

    private ObjectId userId;

    private int xp = 0;
    private int diamond = 0;
    private Rank rank;
    private int currentStreak = 0;
    private int longestStreak = 0;
    private LocalDate lastStudyDate = LocalDate.now().minusDays(1);
    private boolean isReceivedSeasonReward;
}
