package cfgroningen.tarski.hash;

import java.io.IOException;
import java.io.InputStream;

import lombok.Getter;

public class CircleHashInputStream extends InputStream {
    private InputStream underlyingStream;

    public CircleHashInputStream(InputStream underlyingStream) {
        this.underlyingStream = underlyingStream;
    }

    @Getter
    private long checksum = 0;
    private int shift = 0;

    private long passthrough(byte b) {
        if (b == 13 || b == 10)
            return this.checksum;

        int castedSum = (int) this.checksum;
        castedSum += ~(b << shift ^ b >> 7 & 1) & 0xFF;
        checksum = castedSum &= 0xFFFFFFFF;
        shift = (shift + 1) % 8;
        return this.checksum;
    }

    @Override
    public int read() throws IOException {
        int result = this.underlyingStream.read();
        passthrough((byte) result);

        return result;
    }

}
