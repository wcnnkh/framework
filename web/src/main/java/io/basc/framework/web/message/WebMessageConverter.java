package io.basc.framework.web.message;

import io.basc.framework.net.client.convert.ClientMessageConverter;
import io.basc.framework.net.server.convert.ServerMessageConverter;

public interface WebMessageConverter extends ClientMessageConverter, ServerMessageConverter{
	
}
