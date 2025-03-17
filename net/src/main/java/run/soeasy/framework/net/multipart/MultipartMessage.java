package run.soeasy.framework.net.multipart;

import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.util.io.Resource;

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
