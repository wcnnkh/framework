package run.soeasy.framework.io;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferPool;
import run.soeasy.framework.io.buffer.ThreadLocalBufferPool;
import run.soeasy.framework.io.transfer.DefaultBufferTransferrer;
import run.soeasy.framework.io.transfer.DefaultTextTransferrer;

import java.nio.CharBuffer;

@Getter
public final class Text extends DefaultTextTransferrer implements BufferPool<CharBuffer> {
    public static final int DEFAULT_CHAR_BUFFER_SIZE = Math.min(1024 * 8, Binary.DEFAULT_BYTE_BUFFER_SIZE / 2);
    public static final Text INSTANCE = new Text();

    @NonNull
    private final BufferPool<CharBuffer> bufferPool;

    private Text(){
        this(new ThreadLocalBufferPool<>(() -> CharBuffer.allocate(DEFAULT_CHAR_BUFFER_SIZE)));
    }

    public Text(@NonNull BufferPool<CharBuffer> bufferPool) {
        super(new DefaultBufferTransferrer<>(bufferPool));
        this.bufferPool = bufferPool;
    }

    @Override
    public CharBuffer get() {
        return bufferPool.get();
    }

    @Override
    public void close(@NonNull CharBuffer source) {
        bufferPool.close(source);
    }
}
