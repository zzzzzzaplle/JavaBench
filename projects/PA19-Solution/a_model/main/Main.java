import game.Game;
import io.Deserializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

class Main {

    private static String[] QUIT_RESPONSES = {
            ":q",
            "exit",
            "quit"
    };

    private static String[] UNDO_RESPONSES = {
            ":u",
            "undo"
    };

    public static void main(String[] args) {

        if (args.length == 1 && args[0].equals("--help")) {
            System.out.println("Usage: java -jar PA1.jar");
            System.out.println("Usage: java -jar PA1.jar [file]");
            System.out.println("Usage: java -jar PA1.jar [rows] [cols]");
            return;
        }

        Game g = null;

        try {
            if (args.length == 0) {
                int rows = 8;
                int cols = 8;

                g = new Game(rows, cols);
            } else if (args.length == 1) {
                try {
                    var deserializer = new Deserializer(args[0]);
                    g = deserializer.parseGame();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw e;
                }
            } else if (args.length == 2) {
                int rows = Integer.parseInt(args[0]);
                int cols = Integer.parseInt(args[1]);

                g = new Game(rows, cols);
            }
        } catch (final Throwable tr) {
            tr.printStackTrace();
            System.err.println("Unable to create game: " + tr.getMessage());
            return;
        }

        if (g == null) {
            System.err.println("Unable to create game!");
            return;
        }

        System.out.println("Enter \":q\" to quit the game.");
        System.out.println("Enter \":u\" to undo the last step.");
        System.out.println();

        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            do {
                g.display();
                System.out.print("Enter coordinates for pipe (<col#><row#>): ");
                String input = reader.readLine();

                if (Arrays.stream(QUIT_RESPONSES).anyMatch(it -> it.equalsIgnoreCase(input))) {
                    break;
                }
                if (Arrays.stream(UNDO_RESPONSES).anyMatch(it -> it.equalsIgnoreCase(input))) {
                    if (!g.undoStep()) {
                        System.err.println("No steps to undo!");
                    }
                    continue;
                }

                var col = Character.toUpperCase(input.charAt(0));
                if (!Character.isAlphabetic(col)) {
                    System.err.println("Cannot parse column!");
                    continue;
                }

                var rowStr = input.codePoints()
                        .skip(1)
                        .takeWhile(Character::isDigit)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
                if (rowStr.isBlank()) {
                    System.err.println("Cannot parse row!");
                    continue;
                }
                var row = Integer.parseInt(rowStr);

                if (g.placePipe(row, col)) {
                    g.updateState();
                    if (g.hasWon()) {
                        System.out.println("You win using " + g.getNumOfSteps() + " steps!");
                        break;
                    } else if (g.hasLost()) {
                        System.out.println("You lost!");
                        break;
                    }
                } else {
                    System.err.println("Cannot place pipe on that location!");
                }
            } while (true);
        } catch (IOException e) {
            System.err.println("Cannot read from console!");
            e.printStackTrace();
            return;
        }
    }
}
