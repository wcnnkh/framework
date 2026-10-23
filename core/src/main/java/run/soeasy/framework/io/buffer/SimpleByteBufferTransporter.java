package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SimpleByteBufferTransporter<T extends BufferTransporter<? extends ByteBuffer>>
		implements ByteBufferTransporter {
	@NonNull
	private final T transporter;

	@Override
	public <R extends Throwable, W extends Throwable> void transfer(
			@NonNull BufferReader<? super ByteBuffer, ? extends R> reader,
			@NonNull BufferWriter<? super ByteBuffer, ? extends W> writer) throws IOException, R, W {
		transporter.transfer(reader, writer);
	}
}
