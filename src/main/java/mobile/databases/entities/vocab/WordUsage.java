package mobile.databases.entities.vocab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordUsage {
    private String category;    // "time", "place", "direction", "cause_reason", "contrast", etc.
    private String structure;   // Cấu trúc / cách dùng (e.g. "since + mốc thời gian" hoặc "since + S + V")
    private String meaning;     // Nghĩa tiếng Việt theo cách dùng này
    private String note;        // Lưu ý / mẹo / bẫy thi
    private List<ExampleSentence> examples = new ArrayList<>();
}
