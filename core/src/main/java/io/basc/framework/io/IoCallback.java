package io.basc.framework.io;

import io.basc.framework.util.stream.Callback;

import java.io.IOException;

@FunctionalInterface
public interface IoCallback<S> extends Callback<S, IOException> {
	void call(S source) throws IOException;
}
