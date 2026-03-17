package game.map.cells;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import util.Coordinate;
import util.Direction;
import util.PipePatterns;

import static org.junit.jupiter.api.Assertions.*;

class TerminationCellTest {

    private static Coordinate DEFAULT_COORD = new Coordinate(1, 2);
    private static Direction DEFAULT_DIR = Direction.UP;
    private static TerminationCell.Type DEFAULT_TYPE = TerminationCell.Type.SOURCE;

    private TerminationCell cellUp = null;

    @Test
    void givenCell_assertCorrectProperties() {
        cellUp = new TerminationCell(DEFAULT_COORD, DEFAULT_DIR, DEFAULT_TYPE);

        assertEquals(DEFAULT_COORD, cellUp.coord);
        assertEquals(DEFAULT_DIR, cellUp.pointingTo);
        assertEquals(DEFAULT_TYPE, cellUp.type);
    }

    @Test
    void givenCell_assertSingleCharRepresentation() {
        TerminationCell cellUp = new TerminationCell(DEFAULT_COORD, Direction.UP, DEFAULT_TYPE);
        TerminationCell cellDown = new TerminationCell(DEFAULT_COORD, Direction.DOWN, DEFAULT_TYPE);
        TerminationCell cellLeft = new TerminationCell(DEFAULT_COORD, Direction.LEFT, DEFAULT_TYPE);
        TerminationCell cellRight = new TerminationCell(DEFAULT_COORD, Direction.RIGHT, DEFAULT_TYPE);

        assertEquals(PipePatterns.Unfilled.UP_ARROW, cellUp.toSingleChar());
        assertEquals(PipePatterns.Unfilled.DOWN_ARROW, cellDown.toSingleChar());
        assertEquals(PipePatterns.Unfilled.LEFT_ARROW, cellLeft.toSingleChar());
        assertEquals(PipePatterns.Unfilled.RIGHT_ARROW, cellRight.toSingleChar());
    }

    @Test
    void givenFilledCell_assertSingleCharRepresentation() {
        TerminationCell cellUp = new TerminationCell(DEFAULT_COORD, Direction.UP, DEFAULT_TYPE);
        TerminationCell cellDown = new TerminationCell(DEFAULT_COORD, Direction.DOWN, DEFAULT_TYPE);
        TerminationCell cellLeft = new TerminationCell(DEFAULT_COORD, Direction.LEFT, DEFAULT_TYPE);
        TerminationCell cellRight = new TerminationCell(DEFAULT_COORD, Direction.RIGHT, DEFAULT_TYPE);
        cellUp.setFilled();
        cellDown.setFilled();
        cellLeft.setFilled();
        cellRight.setFilled();

        assertEquals(PipePatterns.Filled.UP_ARROW, cellUp.toSingleChar());
        assertEquals(PipePatterns.Filled.DOWN_ARROW, cellDown.toSingleChar());
        assertEquals(PipePatterns.Filled.LEFT_ARROW, cellLeft.toSingleChar());
        assertEquals(PipePatterns.Filled.RIGHT_ARROW, cellRight.toSingleChar());
    }

    @AfterEach
    void tearDown() {
        cellUp = null;
    }
}
