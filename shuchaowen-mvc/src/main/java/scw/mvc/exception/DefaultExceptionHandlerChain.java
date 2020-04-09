package scw.mvc.exception;

import java.util.Collection;

import scw.mvc.Channel;

public class DefaultExceptionHandlerChain implements ExceptionHandlerChain{
	private final Collection<? extends ExceptionHandler> handlers;
	private final ExceptionHandlerChain chain;
	
	public DefaultExceptionHandlerChain(Collection<? extends ExceptionHandler> handlers, ExceptionHandlerChain chain){
		this.handlers = handlers;
		this.chain = chain;
	}
	
	public Object doHandler(Channel channel, Throwable error) {
		ExceptionHandlerChain actionExceptionHandlerChain = new IteratorExceptionHandlerChain(handlers, chain);
		return actionExceptionHandlerChain.doHandler(channel, error);
	}

}
