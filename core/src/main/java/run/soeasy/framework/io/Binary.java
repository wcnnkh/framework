package run.soeasy.framework.io;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferPool;
import run.soeasy.framework.io.buffer.ThreadLocalBufferPool;
import run.soeasy.framework.io.transfer.DefaultBinaryTransferrer;
import run.soeasy.framework.io.transfer.DefaultBufferTransferrer;

import java.nio.ByteBuffer;

@Getter
public final class Binary extends DefaultBinaryTransferrer implements BufferPool<ByteBuffer> {
    /**
     * 默认字节缓冲区大小（兼顾性能与内存占用）：
     * <ul>
     * <li>最小值：1KB（1024 字节），保证基础处理能力</li>
     * <li>参考值：操作系统页大小（os.pagesize），适配系统 IO 特性</li>
     * <li>最大值：64KB（65536 字节），超过该值无性能提升</li>
     * </ul>
     */
    public static final int DEFAULT_BYTE_BUFFER_SIZE = Math.min(1024 * 64,
            Math.max(1024, Integer.getInteger("os.pagesize", 1024 * 4)));

    public static final Binary INSTANCE = new Binary();

    @NonNull
    private final BufferPool<ByteBuffer> bufferPool;

    private Binary(){
        this(new ThreadLocalBufferPool<>(() -> ByteBuffer.allocate(DEFAULT_BYTE_BUFFER_SIZE)));
    }

    public Binary(@NonNull BufferPool<ByteBuffer> bufferPool) {
        super(new DefaultBufferTransferrer<>(bufferPool));
        this.bufferPool = bufferPool;
    }

    @Override
    public ByteBuffer get() {
        return bufferPool.get();
    }

    @Override
    public void close(@NonNull ByteBuffer source) {
        bufferPool.close(source);
    }
}
