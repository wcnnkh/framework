package io.basc.framework.net.server.convert;

import io.basc.framework.net.convert.MessageConverters;

public class ServerMessageConverters<T extends ServerMessageConverter> extends MessageConverters<T>
		implements ServerMessageConverter {
}
