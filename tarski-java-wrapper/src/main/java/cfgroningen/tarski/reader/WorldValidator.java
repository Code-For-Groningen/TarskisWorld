package cfgroningen.tarski.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import cfgroningen.tarski.hash.CircleHashInputStream;
import cfgroningen.tarski.shape.Position;
import cfgroningen.tarski.shape.Shape;
import cfgroningen.tarski.shape.ShapeType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorldValidator {
    private InputStream stream;

    public WorldValidator(File file) throws FileNotFoundException {
        FileInputStream fileStream = new FileInputStream(file);
        this.stream = fileStream;
    }

    public boolean validate() throws IllegalStateException {
        CircleHashInputStream stream = new CircleHashInputStream(this.stream);
        try (Scanner scanner = new Scanner(stream)) {
            String softwareVersion = "";
            String fileType = "";
            String timestampList = "";

            int lineIndex = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineIndex++;

                if (lineIndex == 1) {
                    softwareVersion = line;
                } else if (lineIndex == 2) {
                    // No checks needed here
                } else if (lineIndex == 3) {
                    fileType = line;
                    break;
                }
            }

            // Make sure the version is (?:[0-9]\.*)+
            if (!softwareVersion.matches("^(?:[0-9]\\.)+$")) {
                throw new IllegalStateException("Invalid software version " + softwareVersion);
            }

            // Make sure fileType is WldF or WldP
            if (!fileType.equals("WldF") && !fileType.equals("WldP")) {
                throw new IllegalStateException("Invalid file type " + fileType);
            }

            // Make sure timestamp list is (?:[CD][0-9]+)+
            if (!timestampList.matches("^(?:[CD][0-9]+)+$")) {
                throw new IllegalStateException("Invalid timestamp list " + timestampList);
            }

            // We now have all the metadata for the file,
            if (fileType.equals("WldF")) {
                // We have a newer type of format, we need timestamp list and top level hash
                timestampList = scanner.nextLine();

                String currentHash = stream.getChecksum() + "";
                String expectedHash = scanner.nextLine();

                currentHash = "S" + currentHash;

                if (!currentHash.equals(expectedHash)) {
                    throw new IllegalStateException(
                            "Invalid top level hash, expected " + expectedHash + " but got "
                                    + currentHash);
                }
            }

            int shapeCount = Integer.parseInt(scanner.nextLine());
            if (shapeCount < 0 || shapeCount > 64) {
                throw new IllegalStateException("Invalid shape count " + shapeCount + " (0 < count < 64)");
            }

            Map<Position, Shape> shapes = new HashMap<>();

            for (int i = 0; i < shapeCount; i++) {
                // Each shape needs 3 lines, type, position and label
                String type = scanner.nextLine().trim();
                String positionLine = scanner.nextLine().trim();
                String label = scanner.nextLine().trim();

                int id = Integer.parseInt(type.split(" ")[0]);
                if (id <= 0 || id > 3) {
                    throw new IllegalStateException("Invalid shape id " + id + " (0 < id < 3)");
                }

                ShapeType shapeType = ShapeType.byNumber(id);
                int size = Integer.parseInt(type.split(" ")[1]);

                if (size <= 0 || size > 3)
                    throw new IllegalStateException("Invalid shape size " + size + " (0 < size < 3)");

                int xPos = Integer.parseInt(positionLine.split(" ")[0]);
                int yPos = Integer.parseInt(positionLine.split(" ")[1]);

                Position position = new Position(xPos, yPos);

                if (!label.startsWith("\'"))
                    throw new IllegalStateException("Label should start with \', got " + label);

                String actualLabel = label.substring(1);
                if (actualLabel.length() == 0)
                    actualLabel = null;

                if (size <= 0 || size > 3)
                    throw new IllegalStateException("Invalid size " + size + " (1 <= size <= 3)");

                Shape shape = new Shape(shapeType, size, actualLabel);
                shapes.put(position, shape);
            }

            String bottomHash = stream.getChecksum() + "";
            bottomHash = "s=" + bottomHash + ";";

            String expectedBottomHash = scanner.nextLine();
            if (!bottomHash.equals(expectedBottomHash)) {
                throw new IllegalStateException(
                        "Invalid bottom hash, expected " + expectedBottomHash + " but got "
                                + bottomHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
