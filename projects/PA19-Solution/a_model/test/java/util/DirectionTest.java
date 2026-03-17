package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DirectionTest {

    private static Coordinate UP_OFFSET = new Coordinate(-1, 0);
    private static Coordinate DOWN_OFFSET = new Coordinate(1, 0);
    private static Coordinate LEFT_OFFSET = new Coordinate(0, -1);
    private static Coordinate RIGHT_OFFSET = new Coordinate(0, 1);

    @Test
    void givenDirections_ifCorrectOppositeDirection_thenSucceed() {
        assertEquals(Direction.DOWN, Direction.UP.getOpposite());
        assertEquals(Direction.UP, Direction.DOWN.getOpposite());
        assertEquals(Direction.RIGHT, Direction.LEFT.getOpposite());
        assertEquals(Direction.LEFT, Direction.RIGHT.getOpposite());
    }

    @Test
    void givenDirections_ifCorrectCoordOffset_thenSucceed() {
        assertEquals(UP_OFFSET, Direction.UP.getOffset());
        assertEquals(DOWN_OFFSET, Direction.DOWN.getOffset());
        assertEquals(LEFT_OFFSET, Direction.LEFT.getOffset());
        assertEquals(RIGHT_OFFSET, Direction.RIGHT.getOffset());
    }
}
