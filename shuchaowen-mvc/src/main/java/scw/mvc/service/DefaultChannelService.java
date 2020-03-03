package scw.mvc.service;

import scw.mvc.handler.HandlerChain;

public class DefaultChannelService extends AbstractChannelService {
	private final HandlerChain handlerChain;
	private final long warnExecuteMillisecond;

	public DefaultChannelService(HandlerChain handlerChain,
			long warnExecuteMillisecond) {
		this.handlerChain = handlerChain;
		this.warnExecuteMillisecond = warnExecuteMillisecond;
	}

	@Override
	public HandlerChain getHandlerChain() {
		return handlerChain;
	}

	@Override
	public long getWarnExecuteMillisecond() {
		return warnExecuteMillisecond;
	}
}
