package mobile.model.Entity;

import lombok.Data;

@Data
public class Hitbox {
    private int x1;
    private int y1;
    private int x2;
    private int y2;

    // Damage
    private int damage;

    // Knockback
    private double pushX;     // đẩy ngang
    private double pushY;     // đẩy lên (launch)

    // Knockdown
    private boolean knockdown;

    private boolean launcher;

    // Hit stun (số tick bị đứng hình)
    private int hitStun;

    // Hit pause (đứng hình cả 2)
    private int hitPause;

    // Ưu tiên / multi-hit
    private boolean hitOnce;

    // State chuyển của target
    private String targetState; // e.g. "KNOCKDOWN"

    private String sound;
}
