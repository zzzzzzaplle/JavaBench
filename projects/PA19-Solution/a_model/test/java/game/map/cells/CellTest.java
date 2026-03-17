package game.map.cells;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import util.Coordinate;
import util.Direction;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    private static Coordinate DEFAULT_COORDINATE = new Coordinate(1, 2);

    private Cell cell = null;

    @Test
    void givenUnknownCHar_whenCreateCellFromChar_assertNull() {
        cell = Cell.fromChar('A', DEFAULT_COORDINATE, null);

        assertNull(cell);
    }

    @Test
    void givenWallChar_whenCreateCellFromChar_assertCorrectType() {
        cell = Cell.fromChar('W', DEFAULT_COORDINATE, null);

        assertTrue(cell instanceof Wall);
        assertEquals(DEFAULT_COORDINATE, cell.coord);
    }

    @Test
    void givenWallChar_whenCreateCellFromCharWithTerminationType_assertCorrectType() {
        cell = Cell.fromChar('W', DEFAULT_COORDINATE, TerminationCell.Type.SOURCE);

        assertTrue(cell instanceof Wall);
        assertEquals(DEFAULT_COORDINATE, cell.coord);
    }

    @Test
    void givenCellChar_whenCreateCellFromChar_assertCorrectType() {
        cell = Cell.fromChar('.', DEFAULT_COORDINATE, null);

        assertTrue(cell instanceof FillableCell);
        assertEquals(DEFAULT_COORDINATE, cell.coord);
    }

    @Test
    void givenCellChar_whenCreateCellFromCharWithTerminationType_assertCorrectType() {
        cell = Cell.fromChar('.', DEFAULT_COORDINATE, TerminationCell.Type.SOURCE);

        assertTrue(cell instanceof FillableCell);
        assertEquals(DEFAULT_COORDINATE, cell.coord);
    }

    @Test
    void givenTerminationCellChar_whenCreateCellFromCharWithParams_assertCorrectType() {
        cell = Cell.fromChar('^', DEFAULT_COORDINATE, TerminationCell.Type.SOURCE);

        assertTrue(cell instanceof TerminationCell);
        assertEquals(DEFAULT_COORDINATE, cell.coord);
        assertEquals(Direction.UP, ((TerminationCell) cell).pointingTo);
        assertEquals(TerminationCell.Type.SOURCE, ((TerminationCell) cell).type);
    }

    @Test
    void givenTerminationCellChar_whenCreateCellFromCharWithoutParams_assertNull() {
        cell = Cell.fromChar('^', DEFAULT_COORDINATE, null);

        assertNull(cell);
    }

    @AfterEach
    void tearDown() {
        cell = null;
    }
}
