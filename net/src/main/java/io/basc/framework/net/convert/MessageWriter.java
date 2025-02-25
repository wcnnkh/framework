package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public interface MessageWriter {
	boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, MimeType contentType);

	void writeTo(@NonNull Source source, MediaType contentType, @NonNull Request request,
			@NonNull OutputMessage outputMessage) throws IOException;
}
