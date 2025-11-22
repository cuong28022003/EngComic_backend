package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpriteData {
    private String imageSrc;
    private int framesMax;
    private List<Integer> hitFrames;
    private AttackBox attackBox;     // Box riêng cho đòn đánh này
    private Double damage;           // Damage của đòn tấn công
    private Double knockback;        // Độ đẩy lùi
}
