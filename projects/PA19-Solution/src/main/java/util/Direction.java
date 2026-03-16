package util;

import org.jetbrains.annotations.NotNull;

/**
 * getOpposite() 的真正用途：检查管道连接
 * Represents a direction in reference to a {@link game.map.cells.Cell}.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    /**
     * @return The opposite direction of {@code this}.
     */
    @NotNull
    public Direction getOpposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                throw new IllegalStateException("Unknown direction");
        }
    }

    /**
     * @return A unit coordinate offset as expressed by {@code this} coordinate.
     */
    @NotNull
    public Coordinate getOffset() {
        switch (this) {
            case UP:
                return new Coordinate(-1, 0);
            case DOWN:
                return new Coordinate(1, 0);
            case LEFT:
                return new Coordinate(0, -1);
            case RIGHT:
                return new Coordinate(0, 1);
            default:
                throw new IllegalStateException("Unknown direction");
        }
    }
}
