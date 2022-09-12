package cfgroningen.tarski.world;

import java.util.Map;

import cfgroningen.tarski.shape.Position;
import cfgroningen.tarski.shape.Shape;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class World {
    private String softwareVersion = "7.1.0.17405";
    private String operatingSystem = "wnds:Windows Server 20126.2";
    private String fileType = "WldF";
    private String timestampsList;
    private String toplevelHash;

    @NonNull
    private Map<Position, Shape> shapes;

    private String bottomHash;
}
