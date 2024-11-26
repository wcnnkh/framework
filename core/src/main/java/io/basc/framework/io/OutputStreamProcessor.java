package io.basc.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import io.basc.framework.util.Pipeline;

public interface OutputStreamProcessor<T> extends Pipeline<OutputStream, T, IOException> {

	public static OutputStreamProcessor<OutputStreamWriter> toOutputStreamWriter() {
		return (os) -> new OutputStreamWriter(os);
	}

	public static OutputStreamProcessor<OutputStreamWriter> toOutputStreamWriter(Charset charset) {
		return (os) -> new OutputStreamWriter(os, charset);
	}

	public static OutputStreamProcessor<OutputStreamWriter> toOutputStreamWriter(String charsetName) {
		return (os) -> new OutputStreamWriter(os, charsetName);
	}
}
