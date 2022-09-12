package cfgroningen.tarski.shape;

public enum ShapeType {
    PYRAMID, CUBE, DODECAHEDRON;

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
