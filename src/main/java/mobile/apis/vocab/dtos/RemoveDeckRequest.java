package mobile.apis.vocab.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoveDeckRequest {
    @NotEmpty(message = "Danh sách thẻ từ không được để trống")
    private List<String> cardIds;
    @NotBlank(message = "Vui lòng cung cấp bộ thẻ cần gỡ")
    private String deckId;
}