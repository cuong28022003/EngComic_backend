package mobile.businesses.boundaries.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.databases.entities.game.CharacterSpriteEntity;

public interface SaveCharacterSpritesBoundary {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private CharacterSpriteEntity entity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private CharacterSpriteEntity data;
    }

    Response execute(Request request);
}
