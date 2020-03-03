package scw.mvc.context;

import scw.mvc.Channel;
import scw.mvc.handler.Handler;
import scw.mvc.handler.HandlerChain;

public final class ContextHandler implements Handler{

	public void doHandler(Channel channel, HandlerChain chain) throws Throwable {
		ContextManager.doHandler(channel, chain);
	}

}
