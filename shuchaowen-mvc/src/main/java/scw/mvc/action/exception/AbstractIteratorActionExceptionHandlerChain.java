package scw.mvc.action.exception;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public abstract class AbstractIteratorActionExceptionHandlerChain implements
		ActionExceptionHandlerChain {
	private final ActionExceptionHandlerChain chain;

	public AbstractIteratorActionExceptionHandlerChain(ActionExceptionHandlerChain chain) {
		this.chain = chain;
	}

	public Object doHandler(Channel channel, Action action, Throwable error) {
		ActionExceptionHandler handler = getNextActionExceptionHandler(channel,
				action, error);
		if (handler == null) {
			return chain == null ? null : chain.doHandler(channel, action,
					error);
		}
		
		return handler.doHandler(channel, action, error, this);
	}

	protected abstract ActionExceptionHandler getNextActionExceptionHandler(
			Channel channel, Action action, Throwable error);
}
