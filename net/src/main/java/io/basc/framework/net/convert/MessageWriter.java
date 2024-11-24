package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;

public interface MessageWriter {
	boolean isWriteable(TypeDescriptor typeDescriptor, @Nullable MimeType contentType);

	void writeTo(ValueWrapper source, @Nullable MimeType contentType, OutputMessage target) throws IOException;
}
