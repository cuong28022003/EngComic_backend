package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterStats {
    private int health;
    private int attack;
    private int defense;
    private double speed;
    private double gravity;
}
