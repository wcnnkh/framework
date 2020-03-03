package scw.mvc.handler;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;

public class IteratorHandlerChain extends AbstractIteratorHandlerChain{
	private Iterator<? extends Handler> iterator;
	
	public IteratorHandlerChain(Collection<? extends Handler> handlers, HandlerChain chain) {
		super(chain);
		if(!CollectionUtils.isEmpty(handlers)){
			iterator = handlers.iterator();
		}
	}

	@Override
	protected Handler getNextChannelHandler(Channel channel)
			throws Throwable {
		if(iterator == null){
			return null;
		}
		
		if(iterator.hasNext()){
			return iterator.next();
		}
		
		return null;
	}

}
