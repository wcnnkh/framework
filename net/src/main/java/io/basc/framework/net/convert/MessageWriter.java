package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Any;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;
import lombok.NonNull;

public interface MessageWriter {
	boolean isWriteable(@NonNull TypeDescriptor typeDescriptor, MimeType contentType);

	void writeTo(@NonNull Any source, MimeType contentType, @NonNull OutputMessage target) throws IOException;
}
