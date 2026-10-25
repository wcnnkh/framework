package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.DirectCodec;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.BufferWriter;

public interface DirectStringCodec extends StringCodec, DirectCodec<String, String> {

	@Override
	default StringDecoder decoder() {
		return new StringDecoder() {

			@Override
			public <R extends Throwable, W extends Throwable> void transfer(
					@NonNull BufferReader<? super CharBuffer, ? extends R> reader,
					@NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, R, W {
				String source = IOUtils.text().toCharSequence(reader).toString();
				String target = DirectStringCodec.this.decode(source);
				writer.write(CharBuffer.wrap(target));
			}
		};
	}

	@Override
	default StringEncoder encoder() {
		return new StringEncoder() {

			@Override
			public <R extends Throwable, W extends Throwable> void transfer(
					@NonNull BufferReader<? super CharBuffer, ? extends R> reader,
					@NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, R, W {
				String source = IOUtils.text().toCharSequence(reader).toString();
				String target = DirectStringCodec.this.encode(source);
				writer.write(CharBuffer.wrap(target));
			}
		};
	}

	@Override
	String encode(String source);

	@Override
	String decode(String target);
}
