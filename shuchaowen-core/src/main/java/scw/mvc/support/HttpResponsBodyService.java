package scw.mvc.support;

import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpResponsBodyService implements ResponseBodyService {

	public Object wrapper(Channel channel, Object value) throws Throwable {
		if (channel instanceof HttpChannel) {
			return wrapper((HttpChannel) channel, value);
		}
		return value;
	}

	abstract Object wrapper(HttpChannel channel, Object value) throws Throwable;
}
