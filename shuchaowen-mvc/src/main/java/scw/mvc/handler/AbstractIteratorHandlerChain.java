package scw.mvc.handler;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.Channel;


public abstract class AbstractIteratorHandlerChain implements
		HandlerChain {
	private static Logger logger = LoggerUtils.getLogger(AbstractIteratorHandlerChain.class);
	private final HandlerChain chain;

	public AbstractIteratorHandlerChain(HandlerChain chain) {
		this.chain = chain;
	}

	public final void doHandler(Channel channel) throws Throwable {
		Handler channelHandler = getNextChannelHandler(channel);
		if (channelHandler == null) {
			if (chain == null) {
				lastHandler(channel);
			} else {
				chain.doHandler(channel);
			}
		} else {
			channelHandler.doHandler(channel, this);
		}
	}

	protected abstract Handler getNextChannelHandler(Channel channel)
			throws Throwable;
	
	protected void lastHandler(Channel channel) throws Throwable{
		if(logger.isDebugEnabled()){
			logger.debug("handler not support channel:{}",
					channel.toString());
		}
	}
}
