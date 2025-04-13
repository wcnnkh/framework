package run.soeasy.framework.messaging.multipart;

import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.io.Resource;
import run.soeasy.framework.messaging.InputMessage;

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
