package space.devport.wertik.treasures.system.task;

import lombok.Data;

@Data
public class Pair<X, Y> {

    private final X key;
    private final Y value;
}
