package game;

import game.map.cells.FillableCell;
import game.pipes.Pipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Coordinate;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CellStackTest {

    private CellStack stack;

    @BeforeEach
    void setUp() {
        stack = new CellStack();
    }

    @AfterEach
    void tearDown() {
        stack = null;
    }

    /**
     * Tests whether popping an empty stack returned null.
     */
    @Test
    void givenEmptyStack_ifPop_returnNull() {
        final var poppedCell = assertDoesNotThrow(() -> stack.pop());

        assertNull(poppedCell);
    }

    /**
     * Tests whether pushing into an empty stack does not throw.
     */
    @Test
    void givenEmptyStack_ifPush_noThrow() {
        assertDoesNotThrow(() -> {
            stack.push(new FillableCell(new Coordinate(0, 0)));
            stack.push(new FillableCell(new Coordinate(0, 1)));
            stack.push(new FillableCell(new Coordinate(0, 2)));
        });
    }

    /**
     * Tests whether popping from a one-element stack returns the element.
     */
    @Test
    void givenStack_whenPop_returnElement() {
        final var coord = new Coordinate(0, 0);
        final var pipe = new Pipe(Pipe.Shape.CROSS);
        stack.push(new FillableCell(coord, pipe));

        final var poppedCell = stack.pop();
        assertNotNull(poppedCell);
        assertEquals(coord.row, poppedCell.coord.row);
        assertEquals(coord.col, poppedCell.coord.col);
        assertEquals(pipe, poppedCell.getPipe().orElse(null));
    }

    /**
     * Tests whether a initially-constructed stack has an undo count of 0.
     */
    @Test
    void givenEmptyStack_assertUndoCountEqualsZero() {
        assertEquals(0, stack.getUndoCount());
    }

    /**
     * Tests whether pushing an element into the empty stack changes the undo count.
     * <p>
     * Succeeds if the undo count remains unchanged after pushing.
     * </p>
     */
    @Test
    void givenEmptyStack_whenPush_undoCountDoesNotChange() {
        int originalCount = stack.getUndoCount();

        stack.push(new FillableCell(new Coordinate(0, 0)));

        assertEquals(originalCount, stack.getUndoCount());
    }

    /**
     * Tests whether popping an element from a stack changes the undo count.
     * <p>
     * Succeeds if the undo count increments by 1 after popping.
     * </p>
     */
    @Test
    void givenStack_whenPop_incUndoCount() {
        int originalCount = stack.getUndoCount();

        stack.push(new FillableCell(new Coordinate(0, 0)));
        stack.pop();

        assertEquals(originalCount + 1, stack.getUndoCount());
    }

    /**
     * Tests whether popping an element from an empty stack changes the undo count.
     * <p>
     * Succeeds if the undo count remains unchanged after pushing.
     * </p>
     */
    @Test
    void givenEmptyStack_whenPop_undoCountDoesNotChange() {
        int originalCount = stack.getUndoCount();

        assertNull(stack.pop());

        assertEquals(originalCount, stack.getUndoCount());
    }

    /**
     * Tests whether the push/pop operations has Last-In-First-Out behavior.
     * <p>
     * Succeeds if the elements returned by popping are the reverse order of which the elements are inserted.
     * </p>
     */
    @Test
    void givenEmptyStack_whenPushThenPop_assertLIFOBehavior() {
        final var MAX_COUNT = Pipe.Shape.values().length;
        final var list = new ArrayList<FillableCell>();

        for (int i = 0; i < MAX_COUNT; ++i) {
            var c = new Coordinate(i, i);
            var p = new Pipe(Pipe.Shape.values()[i]);
            var cell = new FillableCell(c, p);
            stack.push(cell);
            list.add(cell);
        }

        FillableCell c;
        while ((c = stack.pop()) != null) {
            final var expected = list.remove(list.size() - 1);
            assertEquals(expected.coord.row, c.coord.row);
            assertEquals(expected.coord.col, c.coord.col);
            assertEquals(expected.getPipe(), c.getPipe());
        }
    }
}
