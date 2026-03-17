package game;

import game.pipes.Pipe;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    void givenGame_ifPipeCanBePlaced_stepCountIncreases() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var initPipes = Collections.singletonList(new Pipe(Pipe.Shape.CROSS));
        final var map = assertDoesNotThrow(() -> Game.fromString(4, 4, 0, cellRep, initPipes));

        assertEquals(0, map.getNumOfSteps());
        assertTrue(map.placePipe(1, 'A'));
        assertEquals(1, map.getNumOfSteps());
    }

    @Test
    void givenGame_ifSkipPipe_stepCountIncreases() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var initPipes = Collections.singletonList(new Pipe(Pipe.Shape.CROSS));
        final var map = assertDoesNotThrow(() -> Game.fromString(4, 4, 0, cellRep, initPipes));

        assertEquals(0, map.getNumOfSteps());
        map.skipPipe();
        assertEquals(1, map.getNumOfSteps());
    }

    @Test
    void givenGame_ifUndoPipe_stepCountIncreases() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var initPipes = Collections.singletonList(new Pipe(Pipe.Shape.CROSS));
        final var map = assertDoesNotThrow(() -> Game.fromString(4, 4, 0, cellRep, initPipes));

        map.placePipe(1, 'A');
        int preNumOfSteps = map.getNumOfSteps();

        assertTrue(map.undoStep());
        assertEquals(preNumOfSteps + 1, map.getNumOfSteps());
    }

    @Test
    void givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var initPipes = Collections.singletonList(new Pipe(Pipe.Shape.CROSS));
        final var map = assertDoesNotThrow(() -> Game.fromString(4, 4, 0, cellRep, initPipes));

        assertEquals(0, map.getNumOfSteps());
        assertFalse(map.placePipe(1, 'B'));
        assertEquals(0, map.getNumOfSteps());
    }


}
