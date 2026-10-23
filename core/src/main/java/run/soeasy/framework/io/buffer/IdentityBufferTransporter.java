package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.Buffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class IdentityBufferTransporter<B extends Buffer, T extends BufferTransporter<B>>
		implements BufferTransporter<B> {
	@NonNull
	protected final T transporter;

	@Override
	public <R extends Throwable, W extends Throwable> void transfer(
			@NonNull BufferReader<? super B, ? extends R> reader, @NonNull BufferWriter<? super B, ? extends W> writer)
			throws IOException, R, W {
		transporter.transfer(reader, writer);
	}

}
