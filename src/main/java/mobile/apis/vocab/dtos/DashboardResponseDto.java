package mobile.apis.vocab.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponseDto {
    private long totalCards;
    private long dueToday;
    private long matureCount;
    private long learningCount;
    private long leechCount;
    private long newCount;
    private Page<CardResponseDto> cards;
}

