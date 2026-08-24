package mobile.apis.vocab.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PracticePromptResponseDto {
    private String deckId;
    private String deckName;
    private int wordCount;
    private List<String> words;
    private String systemPrompt;
    private String jsonTemplate;
}
