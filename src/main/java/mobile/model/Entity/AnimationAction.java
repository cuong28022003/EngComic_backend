package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnimationAction {
    private int animationNumber;
    private String name;

    private List<AnimationFrame> frames;

    private boolean loop;

    private Offset offset;
    
    private String trans; // blend mode: "add", "addalpha", "none"
    private String alpha; // e.g. "256,128" for src,dst
}
