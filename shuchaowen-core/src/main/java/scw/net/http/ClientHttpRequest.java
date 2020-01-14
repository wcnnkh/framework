package scw.net.http;

import scw.net.message.OutputMessage;

public interface ClientHttpRequest extends OutputMessage {
	Method getMethod();

	ClientHttpResponse execute();
}
