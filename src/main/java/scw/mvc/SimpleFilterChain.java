package scw.mvc;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;

public class SimpleFilterChain implements FilterChain{
	private Iterator<Filter> iterator;
	
	public SimpleFilterChain(Collection<Filter> filters){
		if(!CollectionUtils.isEmpty(filters)){
			this.iterator = filters.iterator();
		}
	}
	
	public Object doFilter(Channel channel) throws Throwable {
		if(iterator == null){
			return null;
		}
		
		if(iterator.hasNext()){
			return iterator.next().doFilter(channel, this);
		}
		return null;
	}

}
