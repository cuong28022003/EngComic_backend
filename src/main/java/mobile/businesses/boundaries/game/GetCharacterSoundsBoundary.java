package mobile.businesses.boundaries.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.databases.entities.game.CharacterSoundEntity;

public interface GetCharacterSoundsBoundary {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String characterId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private CharacterSoundEntity data;
    }

    Response execute(Request request);
}
