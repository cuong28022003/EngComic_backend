package mobile.apis.pendingitem.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePendingItemRequest {
    @NotBlank
    private String content;         // "decisive" or "reach a decision"
    private String sourceType;      // "family" | "collocation" | "synonym" | "manual"
    private String sourceCardId;    // Hex string of source card if any
}
