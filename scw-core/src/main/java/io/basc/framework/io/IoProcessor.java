package io.basc.framework.io;

import io.basc.framework.util.stream.Processor;

import java.io.IOException;

@FunctionalInterface
public interface IoProcessor<S, T> extends Processor<S, T, IOException> {
	T process(S source) throws IOException;
}
