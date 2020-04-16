package scw.mvc.service;

import scw.mvc.exception.ExceptionHandlerChain;
import scw.mvc.output.Output;

public class DefaultChannelService extends AbstractChannelService {
	private final FilterChain handlerChain;
	private final long warnExecuteMillisecond;
	private final Output output;
	private final ExceptionHandlerChain exceptionHandlerChain;

	public DefaultChannelService(FilterChain handlerChain,
			long warnExecuteMillisecond, Output output,
			ExceptionHandlerChain exceptionHandlerChain) {
		this.handlerChain = handlerChain;
		this.warnExecuteMillisecond = warnExecuteMillisecond;
		this.output = output;
		this.exceptionHandlerChain = exceptionHandlerChain;
	}

	@Override
	public FilterChain getHandlerChain() {
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
