package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;

public interface MessageReader {
	boolean isReadable(TypeDescriptor typeDescriptor, @Nullable MimeType contentType);

	Object readFrom(TypeDescriptor typeDescriptor, InputMessage source) throws IOException;
}
