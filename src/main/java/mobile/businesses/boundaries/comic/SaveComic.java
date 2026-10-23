package mobile.businesses.boundaries.comic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.comic.dtos.SavedResponseDto;

public interface SaveComic {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String userId;
        private String comicId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private SavedResponseDto saved;
    }

    Response execute(Request request);
}
