package mobile.model.Entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Map;

@Data
@Document(collection = "character_animation")
public class CharacterAnimation {
    @Id
    private String id;
    private String characterId; // ID của thẻ nhân vật
    private String spriteSheetUrl; // URL của sprite sheet
    private int frameWidth;           // Chiều rộng 1 frame
    private int frameHeight;          // Chiều cao 1 frame
    private int fps;                  // Frame per second mặc định

    /**
     * Map action -> frames + row
     * Ví dụ:
     * {
     *   "idle":   {"frames": 4, "row": 0},
     *   "attack": {"frames": 6, "row": 1}
     * }
     */
    private Map<String, AnimationInfo> animations;

    @Data
    public static class AnimationInfo {
        private int frames;   // số lượng frame
        private int row;      // dòng trong sprite sheet
    }

}
