package mobile.model.payload.response.translator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorResponse {
    private String ipa;
    private String meaning;
}
