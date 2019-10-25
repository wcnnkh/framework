package scw.mvc.wrapper;

import scw.mvc.Channel;

public interface ResponseWrapperService {
	Object wrapper(Channel channel, Object value) throws Throwable;
}
