package scw.mvc.handler;

import java.util.Collection;

import scw.mvc.Channel;

public class DefaultHandlerChain implements HandlerChain {
	private final Collection<? extends Handler> handlers;
	private final HandlerChain chain;

	public DefaultHandlerChain(Collection<? extends Handler> handlers,
			HandlerChain chain) {
		this.handlers = handlers;
		this.chain = chain;
	}

	public void doHandler(Channel channel) throws Throwable {
		HandlerChain chain = new IteratorHandlerChain(handlers, this.chain);
		chain.doHandler(channel);
	}

}
