package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Message;
import io.basc.framework.net.Response;
import lombok.NonNull;

public interface MessageReader {
	boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message request);

	Object readFrom(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage request,
			@NonNull Response response) throws IOException;
}
