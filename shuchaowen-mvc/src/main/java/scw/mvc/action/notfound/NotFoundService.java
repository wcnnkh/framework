package scw.mvc.action.notfound;

import scw.mvc.Channel;
import scw.mvc.service.FilterChain;

public interface NotFoundService {
	Object notfound(Channel channel, FilterChain filterChain) throws Throwable;
}
