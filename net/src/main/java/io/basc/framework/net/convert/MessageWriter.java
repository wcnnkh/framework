package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;

public interface MessageWriter {
	boolean isWriteable(TypeDescriptor typeDescriptor, @Nullable MimeType contentType);

	void writeTo(Value source, @Nullable MimeType contentType, OutputMessage target) throws IOException;
}