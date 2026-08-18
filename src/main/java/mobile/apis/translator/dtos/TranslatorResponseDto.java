package mobile.apis.translator.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorResponseDto {
    private String text;
    private String ipa;
    private String meaning;
    private List<String> examples;
}
