package scw.mvc.service;

import scw.mvc.Channel;

public interface FilterChain{
	Object doFilter(Channel channel) throws Throwable;
}
