package scw.mvc.action.exception;

import java.util.Collection;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public class DefaultActionExceptionHandlerChain implements ActionExceptionHandlerChain{
	private final Collection<? extends ActionExceptionHandler> handlers;
	private final ActionExceptionHandlerChain chain;
	
	public DefaultActionExceptionHandlerChain(Collection<? extends ActionExceptionHandler> handlers, ActionExceptionHandlerChain chain){
		this.handlers = handlers;
		this.chain = chain;
	}
	
	public Object doHandler(Channel channel, Action action, Throwable error) {
		ActionExceptionHandlerChain actionExceptionHandlerChain = new IteratorActionExceptionHandlerChain(handlers, chain);
		return actionExceptionHandlerChain.doHandler(channel, action, error);
	}

}
