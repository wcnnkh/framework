package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import lombok.NonNull;

public interface MessageReader {
	boolean isReadable(@NonNull TypeDescriptor typeDescriptor, MimeType contentType);

	Object readFrom(@NonNull TypeDescriptor typeDescriptor, @NonNull InputMessage source) throws IOException;
}
