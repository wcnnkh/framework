package scw.io;

import java.io.IOException;

import scw.util.Callback;

@FunctionalInterface
public interface IoCallback<S> extends Callback<S, IOException> {
	void call(S source) throws IOException;
}
