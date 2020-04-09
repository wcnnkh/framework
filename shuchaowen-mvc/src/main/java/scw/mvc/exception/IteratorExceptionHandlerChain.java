package scw.mvc.exception;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;

public class IteratorExceptionHandlerChain extends AbstractIteratorExceptionHandlerChain{
	private Iterator<? extends ExceptionHandler> iterator;
	
	public IteratorExceptionHandlerChain(Collection<? extends ExceptionHandler> handlers, ExceptionHandlerChain chain) {
		super(chain);
		if(!CollectionUtils.isEmpty(handlers)){
			this.iterator = handlers.iterator();
		}
	}

	@Override
	protected ExceptionHandler getNextExceptionHandler(
			Channel channel, Throwable error) {
		if(iterator == null){
			return null;
		}
		
		return iterator.hasNext()? iterator.next():null;
	}
}
