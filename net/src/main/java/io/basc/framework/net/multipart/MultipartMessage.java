package io.basc.framework.net.multipart;

import io.basc.framework.net.InputMessage;
import io.basc.framework.util.io.Resource;

public interface MultipartMessage extends InputMessage {
	String getName();

	String getOriginalFilename();

	default boolean isFile() {
		return getOriginalFilename() != null;
	}

	long getSize();

	default Resource getResource() {
		return new MultipartFileResource(this);
	}
}
