package scw.mvc.context;

import scw.mvc.Channel;
import scw.mvc.handler.Handler;
import scw.mvc.handler.HandlerChain;

public final class ContextHandler implements Handler{

	public Object doHandler(Channel channel, HandlerChain chain) throws Throwable {
		return ContextManager.doHandler(channel, chain);
	}

}
