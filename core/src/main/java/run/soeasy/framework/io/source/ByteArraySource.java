package run.soeasy.framework.io.source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import lombok.NonNull;

public class ByteArraySource extends ByteArrayOutputStream implements InputStreamSource<InputStream>, OutputStreamSource<OutputStream> {

    public ByteArraySource() {
        super();
    }

    public ByteArraySource(int size) {
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
    
    @Override
    public ByteBuffer readToByteBuffer() throws IOException {
    	return ByteBuffer.wrap(this.buf, 0, this.count).asReadOnlyBuffer();
    }
}
