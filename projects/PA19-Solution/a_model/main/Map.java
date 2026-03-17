package game.map;

import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.map.cells.TerminationCell;
import game.map.cells.Wall;
import game.pipes.Pipe;
import io.Deserializer;
import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;
import util.StringUtils;

import java.util.*;

/**
 * Map of the game.
 */
public class Map {

    private final int rows;
    private final int cols;
    @NotNull
    final Cell[][] cells;

    private TerminationCell sourceCell;
    private TerminationCell sinkCell;

    @NotNull
    private final Set<Coordinate> filledTiles = new HashSet<>();
    private int prevFilledTiles = 0;
    private Integer prevFilledDistance;

    /**
     * Creates a map with size of rows x cols.
     *
     * <p>
     * The map should only contain one source tile in any non-edge cell.
     * The map should only contain one sink tile in any edge cell.
     * The source tile must not point into a wall.
     * The sink tile must point outside the map.
     * </p>
     *
     * @param rows Number of rows.
     * @param cols Number of columns.
     */
    public Map(int rows, int cols) {
        cells = new Cell[rows][cols];

        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                var coord = new Coordinate(r, c);

                if (r == 0 || c == 0) {
                    cells[r][c] = new Wall(coord);
                } else if (r == rows - 1 || c == cols - 1) {
                    cells[r][c] = new Wall(coord);
                } else {
                    cells[r][c] = new FillableCell(coord);
                }
            }
        }

        this.rows = rows;
        this.cols = cols;

        TerminationCell.CreateInfo startCellInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(startCellInfo.coord, startCellInfo.dir, TerminationCell.Type.SOURCE);
        cells[startCellInfo.coord.row][startCellInfo.coord.col] = sourceCell;

        TerminationCell.CreateInfo sinkCellInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(sinkCellInfo.coord, sinkCellInfo.dir, TerminationCell.Type.SINK);
        cells[sinkCellInfo.coord.row][sinkCellInfo.coord.col] = sinkCell;
    }

    /**
     * Creates a map with the given cells.
     *
     * <p>
     * The map should only contain one source tile in any non-edge cell.
     * The map should only contain one sink tile in any edge cell.
     * The source tile must not point into a wall.
     * The sink tile must point outside the map.
     * </p>
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param cells Cells to fill the map.
     */
    public Map(int rows, int cols, @NotNull Cell[][] cells) {
        this.cells = cells;

        this.rows = rows;
        this.cols = cols;

        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                var cell = cells[r][c];

                if (cell instanceof TerminationCell) {
                    var tCell = (TerminationCell) cell;
                    if (tCell.type == TerminationCell.Type.SOURCE) {
                        if (sourceCell != null) {
                            throw new IllegalArgumentException();
                        } else {
                            sourceCell = tCell;
                        }
                    } else if (tCell.type == TerminationCell.Type.SINK) {
                        if (sinkCell != null) {
                            throw new IllegalArgumentException();
                        } else {
                            sinkCell = tCell;
                        }
                    }
                }
            }
        }

        if (sourceCell == null || sinkCell == null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Constructs a map from a map string.
     * <p>
     * This is a convenience method for unit testing.
     * </p>
     *
     * @param rows     Number of rows.
     * @param cols     Number of columns.
     * @param cellsRep String representation of the map, with columns delimited by {@code '\n'}.
     * @return A map with the cells set from {@code cellsRep}.
     * @throws IllegalArgumentException If the map is incorrectly formatted.
     */
    @NotNull
    static Map fromString(int rows, int cols, @NotNull String cellsRep) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);

        return new Map(rows, cols, cells);
    }

    /**
     * Tries to place a pipe at (row, col).
     *
     * @param coord Coordinate to place pipe at.
     * @param pipe  Pipe to place in cell.
     * @return {@code true} if the pipe is placed in the cell, {@code false} otherwise.
     */
    public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Tries to place a pipe at (row, col).
     *
     * <p>
     * Note: You cannot overwrite the pipe of a cell once the cell is occupied.
     * </p>
     * <p>
     * Hint: Remember to check whether the map coordinates are within bounds, and whether the target cell is a
     * {@link FillableCell}.
     * </p>
     *
     * @param row One-Based row number to place pipe at.
     * @param col One-Based column number to place pipe at.
     * @param p   Pipe to place in cell.
     * @return {@code true} if the pipe is placed in the cell, {@code false} otherwise.
     */
    boolean tryPlacePipe(int row, int col, @NotNull Pipe p) {
        if (row <= 0 || row >= rows) {
            return false;
        }
        if (col <= 0 || col >= cols) {
            return false;
        }

        if (!(cells[row][col] instanceof FillableCell)) {
            return false;
        }

        var cell = (FillableCell) (cells[row][col]);
        if (cell.getPipe().isPresent()) {
            return false;
        };

        cells[row][col] = new FillableCell(new Coordinate(row, col), p);
        return true;
    }

    @NotNull
    private TerminationCell.CreateInfo generateStartCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            int row = rng.nextInt(rows);
            int col = rng.nextInt(cols);
            coord = new Coordinate(row, col);

            int dir = rng.nextInt(4);
            direction = Direction.values()[dir];

            if (row == 0 || row == rows - 1) {
                continue;
            }
            if (col == 0 || col == cols - 1) {
                continue;
            }
            switch (direction) {
                case UP:
                    if (row <= 1) {
                        continue;
                    }
                    break;
                case DOWN:
                    if (row >= rows - 2) {
                        continue;
                    }
                    break;
                case LEFT:
                    if (col <= 1) {
                        continue;
                    }
                    break;
                case RIGHT:
                    if (col >= cols - 2) {
                        continue;
                    }
                    break;
            }

            break;
        } while (true);

        return new TerminationCell.CreateInfo(coord, direction);
    }

    @NotNull
    private TerminationCell.CreateInfo generateEndCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            // false -> X-axis, true -> Y-axis
            boolean axisToClamp = rng.nextInt(2) == 1;
            int row = axisToClamp ? (rng.nextInt(2) == 1 ? rows - 1 : 0) : rng.nextInt(rows - 2) + 1;
            int col = !axisToClamp ? (rng.nextInt(2) == 1 ? cols - 1 : 0) : rng.nextInt(cols - 2) + 1;

            if (row == col) {
                continue;
            }

            coord = new Coordinate(row, col);

            if (axisToClamp) {
                if (row == 0) {
                    direction = Direction.UP;
                } else {
                    direction = Direction.DOWN;
                }
            } else {
                if (col == 0) {
                    direction = Direction.LEFT;
                } else {
                    direction = Direction.RIGHT;
                }
            }

            var adjacentCell = coord.add(direction.getOpposite().getOffset());
            if (adjacentCell.equals(sourceCell.coord)) {
                continue;
            }

            break;
        } while (true);

        return new TerminationCell.CreateInfo(coord, direction);
    }

    /**
     * Displays the current map.
     */
    public void display() {
        final int padLength = Integer.valueOf(rows - 1).toString().length();

        Runnable printColumns = () -> {
            System.out.print(StringUtils.createPadding(padLength, ' '));
            System.out.print(' ');
            for (int i = 0; i < cols - 2; ++i) {
                System.out.print((char) ('A' + i));
            }
            System.out.println();
        };

        printColumns.run();

        for (int i = 0; i < rows; ++i) {
            if (i != 0 && i != rows - 1) {
                System.out.print(String.format("%1$" + padLength + "s", i));
            } else {
                System.out.print(StringUtils.createPadding(padLength, ' '));
            }

            Arrays.stream(cells[i]).forEachOrdered(elem -> System.out.print(elem.toSingleChar()));

            if (i != 0 && i != rows - 1) {
                System.out.print(i);
            }

            System.out.println();
        }

        printColumns.run();
    }

    /**
     * Undoes a step from the map.
     *
     * <p>
     * Effectively replaces the cell with an empty cell in the coordinate specified.
     * </p>
     *
     * @param coord Coordinate to reset.
     * @throws IllegalArgumentException if the cell is not an instance of {@link FillableCell}.
     */
    public void undo(@NotNull final Coordinate coord) {
        if (!(cells[coord.row][coord.col] instanceof FillableCell)) {
            throw new IllegalArgumentException("Cannot undo on non-FillableCell types!");
        }
        cells[coord.row][coord.col] = new FillableCell(coord);
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
    }

    /**
     * Fills all pipes that are within {@code distance} units from the {@code sourceCell}.
     *
     * <p>
     * Hint: There are two ways to approach this. You can either iteratively fill the tiles by distance (i.e. filling
     * distance=0, distance=1, etc), or you can save the tiles you have already filled, and fill all adjacent cells of
     * the already-filled tiles. Whichever method you choose is up to you, as long as the result is the same.
     * </p>
     *
     * @param distance Distance to fill pipes.
     */
    public void fillTiles(int distance) {
        if (prevFilledDistance == null) {
            prevFilledDistance = 0;
        }

        while (prevFilledDistance != distance) {
            var currentDistance = prevFilledDistance + 1;
            prevFilledTiles = 0;

            if (currentDistance == 0) {
                filledTiles.add(sourceCell.coord);
                prevFilledTiles = 1;
            } else if (currentDistance == 1) {
                var coord = sourceCell.coord;
                var newCoord = coord.add(sourceCell.pointingTo.getOffset());

                if (!(cells[newCoord.row][newCoord.col] instanceof FillableCell)) {
                    return;
                }
                var cell = (FillableCell) cells[newCoord.row][newCoord.col];
                if (cell.getPipe().isEmpty()) {
                    return;
                }
                if (Arrays.stream(cell.getPipe().get().getConnections()).noneMatch(it -> sourceCell.pointingTo == it.getOpposite())) {
                    return;
                }

                cell.getPipe().get().setFilled();
                filledTiles.add(newCoord);
                prevFilledTiles = 1;
            } else {
                final var traversedTiles = getTraversedCoords();

                for (Coordinate c : traversedTiles) {
                    if (!(cells[c.row][c.col] instanceof FillableCell)) {
                        continue;
                    }

                    var tile = (FillableCell) cells[c.row][c.col];
                    if (tile.getPipe().isEmpty()) {
                        throw new IllegalStateException();
                    }

                    for (Direction d : tile.getPipe().get().getConnections()) {
                        var newCoord = c.add(d.getOffset());

                        if (!(cells[newCoord.row][newCoord.col] instanceof FillableCell)) {
                            continue;
                        }
                        if (traversedTiles.stream().anyMatch(it -> it.equals(newCoord))) {
                            continue;
                        }
                        var cell = (FillableCell) cells[newCoord.row][newCoord.col];
                        if (cell.getPipe().isEmpty()) {
                            continue;
                        }
                        if (Arrays.stream(cell.getPipe().get().getConnections()).noneMatch(it -> d == it.getOpposite())) {
                            continue;
                        }

                        cell.getPipe().get().setFilled();
                        filledTiles.add(newCoord);
                    }
                }

                prevFilledTiles = filledTiles.size() - traversedTiles.size();
            }

            prevFilledDistance = currentDistance;
        }
    }

    /**
     * Checks whether there exists a path from {@code sourceCell} to {@code sinkCell}.
     *
     * <p>
     * The game is won when the player must place pipes on the map such that a path is formed from the source tile to the sink tile. One of the approaches to check this is to use Breadth First Search to search for the sink tile along the pipes. You may also use other algorithms or create your own, provided it achieves the same goal.
     * The game is lost when no additional pipes are filled in each round after the Nth round. The value of N' can be configured in the loaded map. In the example maps, N` is set to 10.
     * </p>
     *
     * @return {@code true} if a path exists, else {@code false}.
     */
    public boolean checkPath() {
        // BFS woohoo!
        var coordsTraversed = new ArrayList<Coordinate>();

        Queue<Coordinate> coordsToCheck = new LinkedList<>();
        coordsToCheck.add(sourceCell.coord);

        while (!coordsToCheck.isEmpty()) {
            var thisCoord = coordsToCheck.remove();
            if (coordsTraversed.stream().anyMatch(it -> it.equals(thisCoord))) {
                continue;
            }

            var thisCell = cells[thisCoord.row][thisCoord.col];
            if (thisCell instanceof TerminationCell) {
                var thisTermCell = (TerminationCell) thisCell;
                if (thisTermCell.type == TerminationCell.Type.SINK) {
                    return true;
                } else {
                    coordsToCheck.add(thisTermCell.coord.add(thisTermCell.pointingTo.getOffset()));
                }
            }

            if (thisCell instanceof FillableCell) {
                ((FillableCell) thisCell).getPipe().ifPresent(pipe -> {
                    for (Direction dir : pipe.getConnections()) {
                        coordsToCheck.add(thisCoord.add(dir.getOffset()));
                    }
                });
            }

            coordsTraversed.add(thisCoord);
        }

        return false;
    }

    /**
     * <p>
     * Hint: The game is lost when a round ends and no pipes are filled during the round. Is
     * there a way to check whether pipes are filled during a round?
     * </p>
     *
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        return prevFilledTiles == 0;
    }
}
