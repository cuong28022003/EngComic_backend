package mobile.model.Entity;

import lombok.Data;

@Data
public class Event {
    private int animationNumber;
    private String attachMode; // anchor | world
    private boolean inheritFacing;
    private Velocity velocity;
    private boolean removeOnStateChange;
    private Boolean shake; // true to trigger screen shake
    private Integer shakeDuration; // duration in frames
    private Double shakeIntensity; // shake amplitude
    private Double scale; // custom scale for the effect
    private String trans; // blend mode: "add", "addalpha", "none"
    private String alpha; // e.g. "256,128" for src,dst
}
