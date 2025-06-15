package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

class EncodedOutputSource<R extends Writer, W extends OutputSource>
		extends EncodedOutputStreamFactory<OutputStream, R, W> implements OutputSource {

	public EncodedOutputSource(@NonNull W source, Object charset,
			@NonNull ThrowingFunction<? super OutputStream, ? extends R, IOException> encoder) {
		super(source, charset, encoder);
	}
}
