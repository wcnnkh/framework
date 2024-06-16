package io.basc.framework.io.convert;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import io.basc.framework.util.function.Processor;

public interface OutputStreamProcessor<T> extends Processor<OutputStream, T, IOException> {
	
	public static OutputStreamProcessor<OutputStreamWriter> toOutputStreamWriter() {
		return (os) -> new OutputStreamWriter(os);
	}
}
