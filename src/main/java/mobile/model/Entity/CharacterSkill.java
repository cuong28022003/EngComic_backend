package mobile.model.Entity;

import lombok.Data;
import mobile.common.SkillType;

import java.util.List;

@Data
public class CharacterSkill {

    private String skillId;

    private String name;

    /**
     * Chuỗi input (↓ ↘ → X)
     */
    private List<String> input;

    /**
     * Thời gian tối đa để hoàn thành input (ms)
     */
    private int bufferTime;

    /**
     * actionCode trong character_animations
     */
    private int animationAction;

    /**
     * tên hitbox trong character_stats
     */
    private String hitbox;

    private int damage;

    /**
     * resource cost (mana / energy)
     */
    private int cost;

    /**
     * cooldown (giây)
     */
    private int cooldown;

    private SkillType type;
}
