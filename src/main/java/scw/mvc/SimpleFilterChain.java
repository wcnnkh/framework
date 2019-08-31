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
	
	public void doFilter(Channel channel) throws Throwable {
		if(iterator == null){
			return ;
		}
		
		if(iterator.hasNext()){
			iterator.next().doFilter(channel, this);
		}
	}

}
