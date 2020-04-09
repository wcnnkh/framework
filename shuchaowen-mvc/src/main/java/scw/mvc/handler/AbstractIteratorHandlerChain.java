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

	public final Object doHandler(Channel channel) throws Throwable {
		Handler channelHandler = getNextChannelHandler(channel);
		if (channelHandler == null) {
			if (chain == null) {
				return lastHandler(channel);
			} else {
				return chain.doHandler(channel);
			}
		} else {
			return channelHandler.doHandler(channel, this);
		}
	}

	protected abstract Handler getNextChannelHandler(Channel channel)
			throws Throwable;
	
	protected Object lastHandler(Channel channel) throws Throwable{
		if(logger.isDebugEnabled()){
			logger.debug("handler not support channel:{}",
					channel.toString());
		}
		return null;
	}
}
