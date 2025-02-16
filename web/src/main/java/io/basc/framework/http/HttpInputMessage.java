package io.basc.framework.http;

import io.basc.framework.net.InputMessage;

public interface HttpInputMessage extends InputMessage, HttpMessage {
	public static interface HttpInputMessageWrapper<W extends HttpInputMessage>
			extends HttpInputMessage, InputMessageWrapper<W>, HttpMessageWrapper<W> {

	}
}