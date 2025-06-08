package mobile.model.payload.request.translator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorRequest {
    private String text; // The text to be translated or processed
}
