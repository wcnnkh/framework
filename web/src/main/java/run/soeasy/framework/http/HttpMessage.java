package run.soeasy.framework.http;

import run.soeasy.framework.net.Message;

public interface HttpMessage extends Message {
	public static interface HttpMessageWrapper<W extends HttpMessage> extends HttpMessage, MessageWrapper<W> {
		@Override
		default HttpHeaders getHeaders() {
			return getSource().getHeaders();
		}
	}

	HttpHeaders getHeaders();
}