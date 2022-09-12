package cfgroningen.tarski.shape;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShapeType {
    PYRAMID(1),
    CUBE(2),
    DODECAHEDRON(3);

    private int id;

    public static ShapeType byNumber(int number) {
        switch (number) {
            case 1:
                return PYRAMID;
            case 2:
                return CUBE;
            case 3:
                return DODECAHEDRON;
            default:
                throw new IllegalArgumentException("Unknown shape type " + number);
        }
    }
}
