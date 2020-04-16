package scw.mvc.service;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;

public class IteratorFilterChain extends AbstractIteratorFilterChain{
	private Iterator<? extends Filter> iterator;
	
	public IteratorFilterChain(Collection<? extends Filter> handlers, FilterChain chain) {
		super(chain);
		if(!CollectionUtils.isEmpty(handlers)){
			iterator = handlers.iterator();
		}
	}

	@Override
	protected Filter getNextFilter(Channel channel)
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
