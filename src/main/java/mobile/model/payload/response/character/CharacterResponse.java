package mobile.model.payload.response.character;

import lombok.*;
import mobile.model.Entity.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {
    private String id;
    private String name;
    private String rarity;
    private String imageUrl;
    private String description;
    private Pack pack;
    private int bonusXp;
    private int bonusDiamond;
    private String version; // phiên bản của thẻ, dùng để quản lý các thay đổi trong tương lai

    private Map<String, Integer> skillsUsagePerDay; // {"DOUBLE_XP": 1, "SHOW_ANSWER": 2}

    private LocalDateTime obtainedAt; // ngày nhận thẻ

   private String spriteSheetUrl = ""; // URL của sprite sheet, mặc định là chuỗi rỗng
    private int frameWidth = 0;         // Chiều rộng 1 frame, mặc định là 0
    private int frameHeight = 0;        // Chiều cao 1 frame, mặc định là 0
    private int fps = 0;                // FPS, mặc định là 0
    private Map<String, CharacterAnimation.AnimationInfo> animations = Map.of(); // Mặc định là Map rỗng

    @Data
    public static class AnimationInfo {
        private int frames = 0;   // Số lượng frame, mặc định là 0
        private int row = 0;      // Dòng trong sprite sheet, mặc định là 0
    }

    private double scale;
    private Position position;
    private int width;
    private int height;

    private Offset offset;
    private AttackBox attackBox;
    private CharacterStats stats;
    private Map<String, SpriteData> sprites;
    private String transformationCharacterId; // ID của nhân vật biến hình
}
