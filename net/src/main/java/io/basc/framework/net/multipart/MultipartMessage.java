package io.basc.framework.net.multipart;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.util.io.Resource;

public interface MultipartMessage extends InputMessage {
	public static final TypeDescriptor TYPE_DESCRIPTOR = TypeDescriptor.valueOf(MultipartMessage.class);

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
