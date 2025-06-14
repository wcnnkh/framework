package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingFunction;

@RequiredArgsConstructor
@Getter
public class StandardDecodedInputStreamFactory<T extends InputStream, R extends Reader, W extends InputStreamFactory<T>>
		implements DecodedInputStreamFactory<T, R, W> {
	@NonNull
	private final W source;
	@NonNull
	private final ThrowingFunction<? super T, ? extends R, IOException> decoder;
}
