package cfgroningen.tarski.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import cfgroningen.tarski.shape.Position;
import cfgroningen.tarski.shape.Shape;
import cfgroningen.tarski.shape.ShapeType;
import cfgroningen.tarski.world.World;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorldReader {
    private InputStream stream;

    public WorldReader(File file) throws FileNotFoundException {
        FileInputStream fileStream = new FileInputStream(file);
        this.stream = fileStream;
    }

    public World parse() throws IOException {
        try (Scanner scanner = new Scanner(stream)) {

            String softwareVersion = "";
            String operatingSystem = "";
            String fileType = "";
            String timestampList = "";
            String toplevelHash = "";

            int lineIndex = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineIndex++;

                if (lineIndex == 1) {
                    softwareVersion = line;
                } else if (lineIndex == 2) {
                    operatingSystem = line;
                } else if (lineIndex == 3) {
                    fileType = line;
                    break;
                }
            }

            // We now have all the metadata for the file,
            if (fileType.equals("WldF")) {
                // We have a newer type of format, we need timestamp list and top level hash
                timestampList = scanner.nextLine();
                toplevelHash = scanner.nextLine();
            } else if (!fileType.equals("WldP")) {
                throw new IllegalStateException("Unkown file type " + fileType);
            }

            int shapeCount = Integer.parseInt(scanner.nextLine());
            Map<Position, Shape> shapes = new HashMap<>();

            for (int i = 0; i < shapeCount; i++) {
                // Each shape needs 3 lines, type, position and label
                String type = scanner.nextLine().trim();
                String positionLine = scanner.nextLine().trim();
                String label = scanner.nextLine().trim();

                ShapeType shapeType = ShapeType.byNumber(Integer.parseInt(type.split(" ")[0]));
                int size = Integer.parseInt(type.split(" ")[1]);

                int xPos = Integer.parseInt(positionLine.split(" ")[0]);
                int yPos = Integer.parseInt(positionLine.split(" ")[1]);

                Position position = new Position(xPos, yPos);

                if (!label.startsWith("\'"))
                    throw new IllegalStateException("Label should start with \', got " + label);

                String actualLabel = label.substring(1);
                if (actualLabel.length() == 0)
                    actualLabel = null;

                Shape shape = new Shape(shapeType, size, actualLabel);
                shapes.put(position, shape);
            }

            String finalHash = scanner.nextLine();

            World world = new World(shapes);
            world.setSoftwareVersion(softwareVersion);
            world.setOperatingSystem(operatingSystem);
            world.setFileType(fileType);
            world.setTimestampsList(timestampList);
            world.setToplevelHash(toplevelHash);
            world.setBottomHash(finalHash);

            scanner.close();

            return world;
        } catch (Exception e) {
            throw e;
        }
    }
}
