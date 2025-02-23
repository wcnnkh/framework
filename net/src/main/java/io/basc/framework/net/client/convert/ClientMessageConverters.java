package io.basc.framework.net.client.convert;

import io.basc.framework.core.execution.Parameter;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.convert.MessageConverters;
import io.basc.framework.net.uri.UriComponentsBuilder;

public class ClientMessageConverters<T extends ClientMessageConverter> extends MessageConverters<T>
		implements ClientMessageConverter {

	@Override
	public void writeTo(Parameter parameter, MediaType contentType, UriComponentsBuilder builder) {
		optional().filter((e) -> e.isWriteable(parameter, contentType))
				.ifPresent((e) -> e.writeTo(parameter, contentType, builder));
	}
}
