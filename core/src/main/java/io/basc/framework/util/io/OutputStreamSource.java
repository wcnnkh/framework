package io.basc.framework.util.io;

import java.io.IOException;
import java.io.OutputStream;

import io.basc.framework.util.Target;

@FunctionalInterface
public interface OutputStreamSource<T extends OutputStream> {
	Target<T, IOException> getOutputStream();
}
