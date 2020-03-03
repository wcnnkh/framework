package scw.mvc.handler;

import scw.mvc.Channel;


public interface Handler{
	void doHandler(Channel channel, HandlerChain chain) throws Throwable;
}
