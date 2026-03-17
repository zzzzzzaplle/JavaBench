package game.pipes;

import org.junit.jupiter.api.Test;
import util.Direction;

import static org.junit.jupiter.api.Assertions.*;

public class PipeTest {
    @Test
    void connection() {
        Pipe pipe;
        pipe = new Pipe(Pipe.Shape.HORIZONTAL);
        assertArrayEquals(new Direction[]{Direction.LEFT, Direction.RIGHT}, pipe.getConnections());
        pipe = new Pipe(Pipe.Shape.TOP_LEFT);
        assertArrayEquals(new Direction[]{Direction.UP, Direction.LEFT}, pipe.getConnections());
        pipe = new Pipe(Pipe.Shape.BOTTOM_LEFT);
        assertArrayEquals(new Direction[]{Direction.DOWN, Direction.LEFT}, pipe.getConnections());
        pipe = new Pipe(Pipe.Shape.CROSS);
        assertArrayEquals(Direction.values(), pipe.getConnections());
    }
}
