package mobile.model.Entity;

import lombok.Data;

import java.util.List;

@Data
public class AnimationFrame {
    /**
     * group sprite (map sang CharacterSprite.groups)
     * vd: 0, 200
     */
    private int group;

    /**
     * image index trong group
     * vd: 0, 1
     */
    private int image;

    /**
     * thời gian hiển thị frame (số ticks - 1 tick = 1/60 giây)
     */
    private int duration;

    private List<Hurtbox> hurtboxes;
    private List<Hitbox> hitboxes;
    private String sound;
    private Double volume;
    
    private String trans; // blend mode override for this frame
    private String alpha; // e.g. "256,128"

    private Velocity velocity; // Lực đẩy liên tục trong frame (dùng cho Dash/Leap)
    private Offset positionOffset; // Dịch chuyển tức thời khi bắt đầu frame (dùng cho Teleport)
}
