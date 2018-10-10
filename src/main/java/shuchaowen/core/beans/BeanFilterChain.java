package shuchaowen.core.beans;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import net.sf.cglib.proxy.MethodProxy;

public final class BeanFilterChain {
	private Iterator<BeanFilter> filterIterator;
	
	public BeanFilterChain(List<BeanFilter> filters){
		this.filterIterator = filters.iterator();
	}
	
	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable{
		if (filterIterator != null && filterIterator.hasNext()) {
			return filterIterator.next().doFilter(obj, method, args, proxy, this);
		} else {
			return proxy.invokeSuper(obj, args);
		}
	}
}
