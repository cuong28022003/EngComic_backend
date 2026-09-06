package mobile.businesses.boundaries.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.databases.entities.game.CharacterAnimationEntity;

public interface SaveCharacterAnimationsBoundary {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private CharacterAnimationEntity entity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private CharacterAnimationEntity data;
    }

    Response execute(Request request);
}
