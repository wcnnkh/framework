package run.soeasy.framework.http;

import run.soeasy.framework.net.OutputMessage;

public interface HttpOutputMessage extends OutputMessage, HttpMessage {
	public static interface HttpOutputMessageWrapper<W extends HttpOutputMessage>
			extends HttpOutputMessage, OutputMessageWrapper<W>, HttpMessageWrapper<W> {
		@Override
		default HttpOutputMessage buffered() {
			return getSource().buffered();
		}
	}

	public static class BufferingHttpOutputMessage<W extends HttpOutputMessage> extends BufferingOutputMessage<W>
			implements HttpOutputMessageWrapper<W> {

		public BufferingHttpOutputMessage(W source) {
			super(source);
		}

		@Override
		public HttpOutputMessage buffered() {
			return this;
		}
	}

	@Override
	default HttpOutputMessage buffered() {
		return new BufferingHttpOutputMessage<>(this);
	}

}
