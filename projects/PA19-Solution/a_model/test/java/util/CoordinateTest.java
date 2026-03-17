package util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoordinateTest {

    private Coordinate coord = null;

    @BeforeEach
    void setUp() {
        coord = new Coordinate(1, 2);
    }

    @Test
    void givenCoord_ifRowAndColMatches_thenSucceed() {
        assertEquals(1, coord.row);
        assertEquals(2, coord.col);
    }

    @Test
    void givenCoord_whenCompareSameCoords_assertEquals() {
        final Coordinate expected = new Coordinate(1, 2);

        assertTrue(coord.equals(expected));
    }

    @Test
    void givenCoord_whenCompareSameCoordsAsObject_assertEquals() {
        final Coordinate expected = new Coordinate(1, 2);

        assertEquals(coord, expected);
    }

    @Test
    void givenCoord_whenCompareCoordsWithDifferentRow_assertNotEquals() {
        final Coordinate unexpected = new Coordinate(2, 2);

        assertNotEquals(unexpected, coord);
    }

    @Test
    void givenCoord_whenCompareCoordsWithDifferentCol_assertNotEquals() {
        final Coordinate unexpected = new Coordinate(1, 1);

        assertNotEquals(unexpected, coord);
    }

    @Test
    void givenCoord_whenCompareCoordsWithDifferentRowAndCol_assertNotEquals() {
        final Coordinate unexpected = new Coordinate(2, 1);

        assertNotEquals(unexpected, coord);
    }

    @Test
    void givenCoord_whenCompareWithNull_assertNotEquals() {
        assertNotEquals(null, coord);
    }

    @Test
    void givenCoord_whenCompareWithNotCoord_assertNotEquals() {
        assertNotEquals(new Object(), coord);
    }

    @Test
    void givenCoord_whenAddCoord_assertCorrect() {
        final int diffRow = 1;
        final int diffCol = 2;
        final Coordinate expected = new Coordinate(coord.row + diffRow, coord.col + diffCol);

        assertEquals(expected, coord.add(new Coordinate(diffRow, diffCol)));
    }

    @Test
    void givenCoord_whenAddNegativeCoord_assertCorrect() {
        final int diffRow = -1;
        final int diffCol = -2;
        final Coordinate expected = new Coordinate(coord.row + diffRow, coord.col + diffCol);

        assertEquals(expected, coord.add(new Coordinate(diffRow, diffCol)));
    }

    @AfterEach
    void tearDown() {
        coord = null;
    }
}
