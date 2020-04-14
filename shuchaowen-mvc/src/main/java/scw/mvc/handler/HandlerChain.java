package scw.mvc.handler;

import scw.mvc.Channel;

public interface HandlerChain{
	Object doHandler(Channel channel) throws Throwable;
}
