package scw.mvc.service;

import scw.mvc.Channel;


public interface Filter{
	Object doFilter(Channel channel, FilterChain chain) throws Throwable;
}
