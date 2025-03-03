package io.basc.framework.http;

import io.basc.framework.net.InputMessage;

public interface HttpInputMessage extends InputMessage, HttpMessage {
	public static interface HttpInputMessageWrapper<W extends HttpInputMessage>
			extends HttpInputMessage, InputMessageWrapper<W>, HttpMessageWrapper<W> {

		@Override
		default HttpInputMessage buffered() {
			return getSource().buffered();
		}
	}

	public static class BufferingHttpInputMessage<W extends HttpInputMessage> extends BufferingInputMessage<W>
			implements HttpInputMessageWrapper<W> {

		public BufferingHttpInputMessage(W source) {
			super(source);
		}

		@Override
		public HttpInputMessage buffered() {
			return this;
		}
	}

	default HttpInputMessage buffered() {
		return new BufferingHttpInputMessage<>(this);
	}
}