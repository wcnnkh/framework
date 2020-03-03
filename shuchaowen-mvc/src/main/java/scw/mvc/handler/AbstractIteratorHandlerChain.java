package scw.mvc.handler;

import scw.mvc.Channel;


public abstract class AbstractIteratorHandlerChain implements
		HandlerChain {
	private final HandlerChain chain;

	public AbstractIteratorHandlerChain(HandlerChain chain) {
		this.chain = chain;
	}

	public final void doHandler(Channel channel) throws Throwable {
		Handler channelHandler = getNextChannelHandler(channel);
		if (channelHandler == null) {
			if (chain == null) {
				channel.getLogger().warn("handler not support channel:{}",
						channel.toString());
			} else {
				chain.doHandler(channel);
			}
		} else {
			channelHandler.doHandler(channel, this);
		}
	}

	protected abstract Handler getNextChannelHandler(Channel channel)
			throws Throwable;
}
