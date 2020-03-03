package scw.mvc.action.exception;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;
import scw.mvc.action.Action;

public class IteratorActionExceptionHandlerChain extends AbstractIteratorActionExceptionHandlerChain{
	private Iterator<? extends ActionExceptionHandler> iterator;
	
	public IteratorActionExceptionHandlerChain(Collection<? extends ActionExceptionHandler> handlers, ActionExceptionHandlerChain chain) {
		super(chain);
		if(!CollectionUtils.isEmpty(handlers)){
			this.iterator = handlers.iterator();
		}
	}

	@Override
	protected ActionExceptionHandler getNextActionExceptionHandler(
			Channel channel, Action action, Throwable error) {
		if(iterator == null){
			return null;
		}
		
		return iterator.hasNext()? iterator.next():null;
	}
}
