package scw.mvc.handler;

import scw.mvc.Channel;


public interface Handler{
	Object doHandler(Channel channel, HandlerChain chain) throws Throwable;
}
