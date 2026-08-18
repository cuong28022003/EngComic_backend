package mobile.businesses.interactors.translator;

import lombok.RequiredArgsConstructor;
import mobile.apis.translator.dtos.TranslatorResponseDto;
import mobile.businesses.boundaries.translator.TranslateText;
import mobile.databases.services.TranslatorClientService;
import mobile.domains.translator.TranslatorRules;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslateTextInteractor implements TranslateText {

    private final TranslatorClientService translatorClientService;

    @Override
    public Response execute(Request request) {
        if (!TranslatorRules.isValidText(request.getText())) {
            throw new IllegalArgumentException("Invalid text to translate");
        }

        TranslatorResponseDto dto = translatorClientService.getIpaAndMeaning(request.getText());

        return Response.builder()
                .result(dto)
                .build();
    }
}
