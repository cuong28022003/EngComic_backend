package mobile.businesses.boundaries.translator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.translator.dtos.TranslatorResponseDto;

public interface TranslateText {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private TranslatorResponseDto result;
    }

    Response execute(Request request);
}
