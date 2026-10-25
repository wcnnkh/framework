package run.soeasy.framework.io;

import java.nio.CharBuffer;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferPool;
import run.soeasy.framework.io.buffer.DefaultBufferTransporter;
import run.soeasy.framework.io.buffer.DefaultCharBufferTransporter;
import run.soeasy.framework.io.buffer.NoneBufferPool;

@Getter
public final class Text extends DefaultCharBufferTransporter {
	public static final int DEFAULT_CHAR_BUFFER_SIZE = Math.min(1024 * 8, Binary.DEFAULT_BYTE_BUFFER_SIZE / 2);
	public static final Text INSTANCE = new Text();

	@NonNull
	private final BufferPool<CharBuffer> bufferPool;

	private Text() {
		this(DEFAULT_CHAR_BUFFER_SIZE);
	}

	public Text(int bufferSize) {
		this(new NoneBufferPool<>(bufferSize, CharBuffer::allocate));
	}

	public Text(@NonNull BufferPool<CharBuffer> bufferPool) {
		super(new DefaultBufferTransporter<>(bufferPool));
		this.bufferPool = bufferPool;
	}
}
