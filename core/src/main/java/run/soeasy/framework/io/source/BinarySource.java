package run.soeasy.framework.io.source;

import lombok.NonNull;

import java.io.*;
import java.nio.ByteBuffer;

public class BinarySource extends ByteArrayOutputStream implements InputSource<InputStream>, OutputSource<OutputStream> {

    public BinarySource() {
        super();
    }

    public BinarySource(int size) {
        super(size);
    }

    @Override
    public final @NonNull InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.buf, 0, this.count);
    }

    @Override
    public final @NonNull OutputStream getOutputStream() throws IOException {
        return this;
    }

    public final ByteBuffer readOnly(){
        return ByteBuffer.wrap(this.buf, 0, this.count).asReadOnlyBuffer();
    }
}
