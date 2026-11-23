package run.soeasy.framework.data.orm.naming;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.io.StringEncoder;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.BufferWriter;

public interface NamingStrategy extends StringEncoder {
	@Override
	default <R extends Throwable, W extends Throwable> void transfer(
			@NonNull BufferReader<? super CharBuffer, ? extends R> reader,
			@NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, R, W {
		String source = IOUtils.text().toCharSequence(reader).toString();
		String target = apply(source);
		writer.write(CharBuffer.wrap(target));
	}

	@Override
	String apply(@NonNull String name);
}
