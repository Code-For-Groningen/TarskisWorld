package cfgroningen.tarski.shape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Shape {

    @NonNull
    private ShapeType type;

    private int size = 1;

    /** The label of the shape. Can be null, usually a single character */
    private String label;

    public Shape(ShapeType type, int size) {
        this.type = type;
        this.size = size;
    }

}
