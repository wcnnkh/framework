package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public interface MessageReader {
	boolean isReadable(@NonNull TargetDescriptor targetDescriptor, MimeType contentType);

	Object readFrom(@NonNull TargetDescriptor targetDescriptor, MimeType contentType,
			@NonNull InputMessage inputMessage) throws IOException;
}
