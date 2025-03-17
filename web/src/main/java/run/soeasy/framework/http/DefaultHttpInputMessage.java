package run.soeasy.framework.http;

import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.InputMessage.InputMessageWrapper;
import run.soeasy.framework.util.function.Wrapped;

public class DefaultHttpInputMessage<W extends InputMessage> extends Wrapped<W>
		implements HttpInputMessage, InputMessageWrapper<W> {
	public DefaultHttpInputMessage(W source) {
		super(source);
		this.httpHeaders.putAll(source.getHeaders());
	}

	private HttpHeaders httpHeaders = new HttpHeaders();

	public HttpHeaders getHeaders() {
		return httpHeaders;
	}

	@Override
	public HttpInputMessage buffered() {
		return HttpInputMessage.super.buffered();
	}
}
