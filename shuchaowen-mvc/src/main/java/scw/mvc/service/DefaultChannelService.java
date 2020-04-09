package scw.mvc.service;

import scw.mvc.exception.ExceptionHandlerChain;
import scw.mvc.handler.HandlerChain;
import scw.mvc.output.Output;

public class DefaultChannelService extends AbstractChannelService {
	private final HandlerChain handlerChain;
	private final long warnExecuteMillisecond;
	private final Output output;
	private final ExceptionHandlerChain exceptionHandlerChain;

	public DefaultChannelService(HandlerChain handlerChain,
			long warnExecuteMillisecond, Output output,
			ExceptionHandlerChain exceptionHandlerChain) {
		this.handlerChain = handlerChain;
		this.warnExecuteMillisecond = warnExecuteMillisecond;
		this.output = output;
		this.exceptionHandlerChain = exceptionHandlerChain;
	}

	@Override
	public HandlerChain getHandlerChain() {
		return handlerChain;
	}

	@Override
	public long getWarnExecuteMillisecond() {
		return warnExecuteMillisecond;
	}

	@Override
	protected ExceptionHandlerChain getExceptionHandlerChain() {
		return exceptionHandlerChain;
	}

	@Override
	protected Output getOutput() {
		return output;
	}
}
