package io.basc.framework.net.message.multipart;

import io.basc.framework.io.Resource;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.message.InputMessage;

import java.io.Closeable;

public interface MultipartMessage extends InputMessage, AutoCloseable, Closeable {
	String getName();

	@Nullable
	String getOriginalFilename();

	default boolean isFile() {
		return getOriginalFilename() != null;
	}

	long getSize();

	default Resource getResource() {
		return new MultipartFileResource(this);
	}
	
	@Override
	default void close() {
	}
}
