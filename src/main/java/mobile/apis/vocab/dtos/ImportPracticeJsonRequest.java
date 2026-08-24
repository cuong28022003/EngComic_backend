package mobile.apis.vocab.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportPracticeJsonRequest {
    @NotBlank(message = "jsonContent cannot be blank")
    private String jsonContent;
}
