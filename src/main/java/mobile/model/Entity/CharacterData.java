package mobile.model.Entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "character_data")
public class CharacterData {
    private String id;
    private String characterId;
    private int hp;
    private int mp;
    private double attack;
    private double defense;
    private int airJuggleLimit;
    private int knockdownTime;
    private double speed;
    private double gravity;
    private double jumpForce;
    private double powerCharge;

    private double scale;
}
