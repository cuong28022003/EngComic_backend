package mobile.businesses.boundaries.chapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.chapter.dtos.ChapterResponseDto;

public interface GetChapterDetail {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String chapterId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private ChapterResponseDto chapter;
    }

    Response execute(Request request);
}
