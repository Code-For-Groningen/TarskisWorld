package cfgroningen.tarski.hash;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import lombok.Getter;

public class CircleHashStream extends OutputStream {
    private OutputStream underlyingStream;

    public CircleHashStream(OutputStream underlyingStream) {
        this.underlyingStream = underlyingStream;
    }

    @Getter
    private long checksum = 0;
    private int shift = 0;

    @Override
    public void write(int b) throws IOException {
        underlyingStream.write(b);
        passthrough((byte) b);
    }

    private long passthrough(byte b) {
        if (b == 13 || b == 10)
            return this.checksum;

        int castedSum = (int) this.checksum;
        castedSum += ~(b << shift ^ b >> 7 & 1) & 0xFF;
        checksum = castedSum &= 0xFFFFFFFF;
        shift = (shift + 1) % 8;
        return this.checksum;
    }

    public void appendLine(String line) throws IOException {
        byte[] actualLine = (line + "\r").getBytes(StandardCharsets.UTF_8);
        this.write(actualLine);
    }
}
