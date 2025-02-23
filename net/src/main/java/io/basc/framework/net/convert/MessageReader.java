package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.transform.stereotype.AccessDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public interface MessageReader {
	boolean isReadable(@NonNull AccessDescriptor targetDescriptor, MimeType contentType);

	Object readFrom(@NonNull AccessDescriptor targetDescriptor, @NonNull InputMessage inputMessage) throws IOException;
}
