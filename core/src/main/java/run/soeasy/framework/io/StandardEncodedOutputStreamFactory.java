package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

@Data
public class StandardEncodedOutputStreamFactory<T extends OutputStream, R extends Writer, W extends OutputStreamFactory<T>>
		implements EncodedOutputStreamFactory<T, R, W> {
	@NonNull
	private final W source;
	@NonNull
	private final ThrowingFunction<? super T, ? extends R, IOException> encoder;
}
