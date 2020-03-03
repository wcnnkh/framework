package scw.mvc.action.filter;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;
import scw.mvc.action.Action;

public class IteratorFilterChain extends AbstractIteratorFilterChain {
	private Iterator<? extends Filter> iterator;

	public IteratorFilterChain(Collection<? extends Filter> filters, FilterChain chain) {
		super(chain);
		if(!CollectionUtils.isEmpty(filters)){
			this.iterator = filters.iterator();
		}
	}
	
	@Override
	protected Filter getNextFilter(Channel channel, Action action)
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
