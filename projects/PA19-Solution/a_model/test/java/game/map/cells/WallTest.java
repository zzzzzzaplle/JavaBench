package game.map.cells;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Coordinate;
import util.PipePatterns;

import static org.junit.jupiter.api.Assertions.*;

class WallTest {

    private static Coordinate DEFAULT_COORDINATE = new Coordinate(1, 2);

    private Wall wall = null;

    @BeforeEach
    void setUp() {
        wall = new Wall(DEFAULT_COORDINATE);
    }

    @Test
    void givenWall_assertCorrectCoordinates() {
        assertEquals(DEFAULT_COORDINATE, wall.coord);
    }

    @Test
    void givenWall_assertCorrectSingleCharRepresentation() {
        assertEquals(PipePatterns.WALL, wall.toSingleChar());
    }

    @AfterEach
    void tearDown() {
        wall = null;
    }
}
