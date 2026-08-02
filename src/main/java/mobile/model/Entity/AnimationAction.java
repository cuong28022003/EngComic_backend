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

    // Properties merged from Event
    private String attachMode; // anchor | world | hitspark
    private Boolean inheritFacing;
    private Velocity velocity;
    private Boolean removeOnStateChange;
    private Boolean shake; // true to trigger screen shake
    private Integer shakeDuration; // duration in frames
    private Double shakeIntensity; // shake amplitude
    private Double scale; // custom scale for the effect

    // Nested structures for spawned effects
    private java.util.Map<Integer, java.util.List<AnimationAction>> childActions;
}
