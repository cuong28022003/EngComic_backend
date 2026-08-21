package mobile.apis.reader.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartAttemptRequest {
    private String timeMode; // "full_test", "per_part", "untimed"
    private List<Integer> selectedParts;
    private int part5TargetSeconds;
    private int part6TargetSeconds;
    private int part7TargetSeconds;
}
