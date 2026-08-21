package mobile.databases.entities.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicQuestion {
    private int number;
    private int part;
    private String correctAnswer; // "A", "B", "C", "D"
}
