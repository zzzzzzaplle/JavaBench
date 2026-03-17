import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    @Test
    void mainHelp() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[] { "--help" });
        assertEquals("""
                Usage: java -jar PA1.jar
                Usage: java -jar PA1.jar [file]
                Usage: java -jar PA1.jar [rows] [cols]
                """, outContent.toString());
    }

    @Test
    void gameCreation() {
        ByteArrayInputStream inContent = new ByteArrayInputStream(":q\n".getBytes());
        System.setIn(inContent);

        inContent.reset();
        Main.main(new String[] { });

        inContent.reset();
        Main.main(new String[] { "8", "8" });
    }

    @Test
    void gameDeserialize() {
        ByteArrayInputStream inContent = new ByteArrayInputStream(":q\n".getBytes());
        System.setIn(inContent);

        String path = Objects.requireNonNull(MainTest.class.getClassLoader().getResource("example1.map")).getPath();
        Main.main(new String[] { path });
    }

    @Test
    void gameLoop() {
        String in = ":u\r\n0\n:q\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(in.getBytes());
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setIn(inContent);
        System.setErr(new PrintStream(errContent));

        Main.main(new String[] { });

        String actual = errContent.toString();
        assertTrue(actual.contains("No steps to undo!"));
        assertTrue(actual.contains("Cannot parse column!"));
    }
}
