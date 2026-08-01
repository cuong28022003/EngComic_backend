package mobile.model.Entity;

import lombok.Data;

import java.util.Map;

@Data
public class State {
    private String name;
    private Integer animationNumber;
    private Map<Integer, Event> events; // integer is the frame index to trigger the event

}
