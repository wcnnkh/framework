package scw.mvc.action;

import scw.mvc.Channel;

public interface ResponseWrapperService {
	Object wrapper(Channel channel, Object value) throws Throwable;
}
