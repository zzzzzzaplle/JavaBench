package game.map.cells;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

/**
 * Representation of a cell in the {@link game.map.Map}.
 */
public abstract class Cell implements MapElement {

    @NotNull
    public final Coordinate coord;

    Cell(@NotNull Coordinate coord) {
        this.coord = coord;
    }

    /**
     * Parses a {@link Cell} from a character.
     *
     * <p>
     * Here is the list of characters to their corresponding map element:
     * W: Wall
     * .: Cell
     * ^: Source/Sink pipe pointing upward
     * v: Source/Sink pipe pointing downward
     * <: Source/Sink pipe pointing leftward
     * >: Source/Sink pipe pointing rightward
     * If the character does not represent a {@link TerminationCell}, the {@code terminationType} parameter can be ignored.
     * </p>
     *
     * @param c Character to parse.
     * @param coord Coordinate of the newly created cell.
     * @param terminationType If the character is a termination cell, its type. Otherwise, this argument is ignored and
     *                        can be null.
     * @return A cell based on the given creation parameters, or null if the parameters cannot form a valid Cell.
     */
    public static @Nullable Cell fromChar(char c, @NotNull Coordinate coord, @Nullable TerminationCell.Type terminationType) {
        switch (c) {
            case 'W':
                return new Wall(coord);
            case '.':
                return new FillableCell(coord);
            case '^':
                if (terminationType != null) {
                    return new TerminationCell(coord, Direction.UP, terminationType);
                }
                break;
            case '>':
                if (terminationType != null) {
                    return new TerminationCell(coord, Direction.RIGHT, terminationType);
                }
                break;
            case '<':
                if (terminationType != null) {
                    return new TerminationCell(coord, Direction.LEFT, terminationType);
                }
                break;
            case 'v':
                if (terminationType != null) {
                    return new TerminationCell(coord, Direction.DOWN, terminationType);
                }
                break;
        }

        return null;
    }
}
