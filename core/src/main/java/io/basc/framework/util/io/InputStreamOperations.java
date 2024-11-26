package io.basc.framework.util.io;

import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.util.function.StreamOperations;

public interface InputStreamOperations<T extends InputStream> extends StreamOperations<T, IOException> {
	
}
