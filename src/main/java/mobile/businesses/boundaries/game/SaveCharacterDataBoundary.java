package mobile.businesses.boundaries.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.databases.entities.game.CharacterDataEntity;

public interface SaveCharacterDataBoundary {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private CharacterDataEntity entity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private CharacterDataEntity data;
    }

    Response execute(Request request);
}
