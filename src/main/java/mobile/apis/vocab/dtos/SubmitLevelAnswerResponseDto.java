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
public class SubmitLevelAnswerResponseDto {
    private String cardId;
    private String word;
    private int oldLevel;
    private int newLevel;
    private boolean levelPromoted;
    private String status;           // learning | review | mature | leech
    private int intervalDays;
    private Date nextReviewDate;
    private int wrongCount;
    private boolean isLeech;
    private String message;
}
