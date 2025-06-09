package run.soeasy.framework.core.io.pipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.io.CharsetCapable;

public interface CharsetOutputStreamFactory<T extends OutputStream, W extends OutputStreamFactory<T>>
		extends EncodedOutputStreamFactory<T, Writer, W>, CharsetCapable {
	@Override
	default ThrowingFunction<? super T, ? extends Writer, IOException> getEncoder() {
		return (e) -> new OutputStreamWriter(e, getCharset());
	}
}