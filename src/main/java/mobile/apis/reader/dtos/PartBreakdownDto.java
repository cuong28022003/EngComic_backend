package mobile.apis.reader.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartBreakdownDto {
    private int part;
    private int correctCount;
    private int totalCount;
    private double accuracyPercentage;
    private int targetSeconds;
    private int elapsedSeconds;
    private double avgSecondsPerQuestion;
}
