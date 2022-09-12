package cfgroningen.tarski.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import cfgroningen.tarski.hash.CircleHashOutputStream;
import cfgroningen.tarski.shape.Position;
import cfgroningen.tarski.shape.Shape;
import cfgroningen.tarski.world.World;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorldWriter {
    private World world;

    public void write(OutputStream stream) throws IOException {
        try (CircleHashOutputStream hashStream = new CircleHashOutputStream(stream)) {

            hashStream.appendLine(world.getSoftwareVersion());
            hashStream.appendLine(world.getOperatingSystem());
            hashStream.appendLine(world.getFileType());

            if (world.getFileType().equals("WldF")) {
                hashStream.appendLine(world.getTimestampsList());
                long checksum = hashStream.getChecksum();
                hashStream.appendLine("S" + checksum);
            }
            Map<Position, Shape> shapes = world.getShapes();
            hashStream.appendLine(shapes.size() + "");

            for (Position cr : shapes.keySet()) {
                Shape shape = shapes.get(cr);

                String shapeLine = shape.getType().getId() + " " + shape.getSize();
                hashStream.appendLine(shapeLine);
                hashStream.appendLine(cr.getX() + " " + cr.getY());
                if (shape.getLabel() == null)
                    hashStream.appendLine("\'");
                else
                    hashStream.appendLine("\'" + shape.getLabel());
            }

            System.out.println("Doing final checksum");

            long checksum = hashStream.getChecksum();
            hashStream.appendLine("s=" + checksum + ";");
            hashStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
