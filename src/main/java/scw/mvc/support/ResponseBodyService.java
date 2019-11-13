package scw.mvc.support;

import scw.mvc.Channel;

public interface ResponseBodyService {
	Object responseBody(Channel channel, Object value) throws Throwable;
}
