package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SimpleCharBufferTransporter<T extends BufferTransporter<? extends CharBuffer>>
		implements CharBufferTransporter {
	@NonNull
	private final T transporter;

	@Override
	public <R extends Throwable, W extends Throwable> void transfer(
			@NonNull BufferReader<? super CharBuffer, ? extends R> reader,
			@NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, R, W {
		transporter.transfer(reader, writer);
	}
}
