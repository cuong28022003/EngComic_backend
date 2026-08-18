package mobile.apis.translator;

import lombok.RequiredArgsConstructor;
import mobile.apis.translator.dtos.TranslatorRequestDto;
import mobile.apis.translator.dtos.TranslatorResponseDto;
import mobile.businesses.boundaries.translator.TranslateText;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/translator")
@RequiredArgsConstructor
public class TranslatorController {

    private final TranslateText translateText;

    @PostMapping("/ipa-meaning")
    public ResponseEntity<TranslatorResponseDto> getIpaAndMeaning(@RequestBody TranslatorRequestDto requestDto) {
        TranslateText.Request request = TranslateText.Request.builder()
                .text(requestDto.getText())
                .build();

        TranslateText.Response response = translateText.execute(request);
        return ResponseEntity.ok(response.getResult());
    }
}
