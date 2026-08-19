package mobile.apis.vocab.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchImportResponseDto {
    @Builder.Default
    private List<CardResponseDto> imported = new ArrayList<>();
    @Builder.Default
    private List<String> skipped = new ArrayList<>();
    @Builder.Default
    private List<ImportError> errors = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private String word;
        private List<String> missingFields;
    }
}

