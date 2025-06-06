package mobile.model.payload.response.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.Entity.Character;
import mobile.model.Entity.Pack;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCharacterResponse {
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private String rarity;
    private Pack pack;
    private int bonusXp;
    private int bonusDiamond;
    private String version; // phiên bản của thẻ, dùng để quản lý các thay đổi trong tương lai

    private Map<String, Integer> skillsUsagePerDay; // {"DOUBLE_XP": 1, "SHOW_ANSWER": 2}
    private Map<String, Integer> usedSkills; // {"DOUBLE_XP": 1, "SHOW_ANSWER": 2} - số lần sử dụng kỹ năng trong ngày

    private LocalDateTime obtainedAt; // ngày nhận thẻ
}
