package mobile.apis.vocab.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchImportRequest {
    @NotBlank
    private String jsonContent;
    private String deckId;
    private List<String> promptWords;
}

