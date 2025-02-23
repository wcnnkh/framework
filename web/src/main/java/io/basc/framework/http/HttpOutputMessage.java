package io.basc.framework.http;

import io.basc.framework.net.OutputMessage;

public interface HttpOutputMessage extends OutputMessage, HttpMessage {
	public static interface HttpOutputMessageWrapper<W extends HttpOutputMessage>
			extends HttpOutputMessage, OutputMessageWrapper<W>, HttpMessageWrapper<W> {
	}
}
