package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Representation of a coordinate in {@link game.map.Map}.
 */
public class Coordinate {

    public final int row;
    public final int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Coordinate)) {
            return false;
        }

        return equals((Coordinate) obj);
    }

    public boolean equals(@NotNull Coordinate other) {
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * Adds {@code this} coordinate with another coordinate.
     *
     * @param other Other coordinate to add from.
     * @return New coordinate as a result of the addition.
     */
    @NotNull public Coordinate add(@NotNull Coordinate other) {
        return new Coordinate(this.row + other.row, this.col + other.col);
    }
}
