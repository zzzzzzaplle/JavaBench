package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

public class Pipe implements MapElement {

    @NotNull
    private final Shape shape;
    private boolean filled = false;

    /**
     * Creates a new pipe with a given shape.
     *
     * @param shape Shape of the pipe.
     */
    public Pipe(@NotNull Shape shape) {
        this.shape = shape;
    }

    /**
     * Sets the pipe as filled.
     */
    public void setFilled() {
        filled = true;
    }

    /**
     * @return Whether this pipe is filled.
     */
    public boolean getFilled() {
        return filled;
    }

    /**
     * @return List of connections for this pipe.
     * @throws IllegalStateException if {@code this} pipe cannot be identified.
     */
    public @NotNull Direction[] getConnections() {
        switch (shape) {
            case HORIZONTAL:
                return new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL:
                return new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT:
                return new Direction[]{Direction.UP, Direction.LEFT};
            case TOP_RIGHT:
                return new Direction[]{Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT:
                return new Direction[]{Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT:
                return new Direction[]{Direction.DOWN, Direction.RIGHT};
            case CROSS:
                return Direction.values();
            default:
                throw new IllegalStateException("Unknown shape");
        }
    }

    /**
     * @return The character representation of this pipe. Note that the representation is different for filled and
     * unfilled pipes.
     */
    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    /**
     * Converts a String to a Pipe.
     *
     * <p>
     * Here is the list of ASCII representation to the pipes:
     * TL: Top-Left
     * TR: Top-Right
     * BL: Bottom-Left
     * BR: Bottom-Right
     * HZ: Horizontal
     * VT: Vertical
     * CR: Cross
     * </p>
     *
     * @param rep String representation of the pipe.
     * @return Pipe identified by the string.
     * @throws IllegalArgumentException if the String does not represent a known pipe.
     */
    public static @NotNull Pipe fromString(@NotNull String rep) {
        String actualRep = rep.trim();

        Pipe.Shape shape;
        switch (actualRep) {
            case "HZ":
                shape = Shape.HORIZONTAL;
                break;
            case "VT":
                shape = Shape.VERTICAL;
                break;
            case "TL":
                shape = Shape.TOP_LEFT;
                break;
            case "TR":
                shape = Shape.TOP_RIGHT;
                break;
            case "BL":
                shape = Shape.BOTTOM_LEFT;
                break;
            case "BR":
                shape = Shape.BOTTOM_RIGHT;
                break;
            case "CR":
                shape = Shape.CROSS;
                break;
            default:
                throw new IllegalArgumentException("Unknown shape");
        }

        return new Pipe(shape);
    }

    public enum Shape {
        HORIZONTAL(PipePatterns.Filled.HORIZONTAL, PipePatterns.Unfilled.HORIZONTAL),
        VERTICAL(PipePatterns.Filled.VERTICAL, PipePatterns.Unfilled.VERTICAL),
        TOP_LEFT(PipePatterns.Filled.TOP_LEFT, PipePatterns.Unfilled.TOP_LEFT),
        TOP_RIGHT(PipePatterns.Filled.TOP_RIGHT, PipePatterns.Unfilled.TOP_RIGHT),
        BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT, PipePatterns.Unfilled.BOTTOM_LEFT),
        BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT, PipePatterns.Unfilled.BOTTOM_RIGHT),
        CROSS(PipePatterns.Filled.CROSS, PipePatterns.Unfilled.CROSS);

        final char filledChar;
        final char unfilledChar;

        Shape(char filled, char unfilled) {
            this.filledChar = filled;
            this.unfilledChar = unfilled;
        }

        char getCharByState(boolean isFilled) {
            return isFilled ? filledChar : unfilledChar;
        }
    }
}
