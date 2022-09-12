package cfgroningen.tarski.world;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cfgroningen.tarski.reader.WorldReader;
import cfgroningen.tarski.shape.Position;
import cfgroningen.tarski.shape.Shape;
import cfgroningen.tarski.shape.ShapeType;
import cfgroningen.tarski.writer.WorldWriter;

public class WorldWriterTest {

    @Test
    public void tryWritingWorldModifiedStuff() throws IOException {
        World world = new World();

        Map<Position, Shape> shapes = new HashMap<>();

        shapes.put(new Position(3, 3), new Shape(ShapeType.CUBE, 1, "g"));

        world.setShapes(shapes);

        world.setSoftwareVersion("7.1.0.17405");
        world.setOperatingSystem("windows:something");
        world.setFileType("WldF");
        world.setTimestampsList("C0");

        ByteArrayOutputStream strm = new ByteArrayOutputStream();

        WorldWriter writer = new WorldWriter(world);
        writer.write(strm);

        ByteArrayInputStream inp = new ByteArrayInputStream(strm.toByteArray());
        WorldReader reader = new WorldReader(inp);
        reader.parse();
    }
}
