package run.soeasy.framework.core.io.pipeline;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;

import lombok.NonNull;

public class DefaultEncodedOutputStreamFactory<T extends OutputStream, W extends OutputStreamFactory<T>>
		extends StandardEncodedOutputStreamFactory<T, Writer, W> {

	public DefaultEncodedOutputStreamFactory(@NonNull W source) {
		super(source, OutputStreamWriter::new);
	}

	public DefaultEncodedOutputStreamFactory(@NonNull W source, @NonNull CharsetEncoder charsetEncoder) {
		super(source, (e) -> new OutputStreamWriter(e, charsetEncoder));
	}
}
