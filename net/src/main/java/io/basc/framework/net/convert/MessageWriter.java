package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import lombok.NonNull;

public interface MessageWriter {
	boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message response);

	void writeTo(@NonNull Source source, @NonNull Request request, @NonNull OutputMessage response) throws IOException;
}
