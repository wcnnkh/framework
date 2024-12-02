package io.basc.framework.util.io;

import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.util.Target;

@FunctionalInterface
public interface InputStreamSource<T extends InputStream> {
	Target<T, IOException> getInputStream();
}
