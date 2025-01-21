package io.basc.framework.net.server;

import java.net.InetSocketAddress;

import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.pattern.PathPattern;
import io.basc.framework.util.attribute.EditableAttributes;

public interface ServerRequest extends Request, InputMessage, EditableAttributes<String, Object> {
	InetSocketAddress getLocalAddress();

	InetSocketAddress getRemoteAddress();

	PathPattern getPattern();
}
