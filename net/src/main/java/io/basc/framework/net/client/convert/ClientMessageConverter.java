package io.basc.framework.net.client.convert;

import io.basc.framework.core.execution.Parameter;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.convert.MessageConverter;
import io.basc.framework.net.uri.UriComponentsBuilder;

public interface ClientMessageConverter extends MessageConverter {
	void writeTo(Parameter parameter, MediaType contentType, UriComponentsBuilder builder);
}