package scw.mvc.wrapper;

import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpResponseWrapperService implements ResponseWrapperService {

	public Object wrapper(Channel channel, Object value) throws Throwable {
		if (channel instanceof HttpChannel) {
			return wrapper((HttpChannel) channel, value);
		}
		return value;
	}

	abstract Object wrapper(HttpChannel channel, Object value) throws Throwable;
}
