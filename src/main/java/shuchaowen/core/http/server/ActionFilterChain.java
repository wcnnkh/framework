package shuchaowen.core.http.server;

import java.util.Iterator;
import java.util.List;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.invoke.Invoker;

public final class ActionFilterChain implements FilterChain {
	private final BeanFactory beanFactory;
	private final Iterator<Filter> filterIterator;
	private final Invoker invoke;
	private final MethodParameter[] paramInfos;

	public ActionFilterChain(BeanFactory beanFactory, Invoker invoke, MethodParameter[] paramInfos, List<Filter> filterList) {
		this.beanFactory = beanFactory;
		this.invoke = invoke;
		this.paramInfos = paramInfos;
		this.filterIterator = filterList == null? null:filterList.iterator();
	}

	public void doFilter(Request request, Response response) throws Throwable {
		if (filterIterator != null && filterIterator.hasNext()) {
			filterIterator.next().doFilter(request, response, this);
		} else {
			Object[] args = new Object[paramInfos.length];
			for (int i = 0; i < paramInfos.length; i++) {
				args[i] = paramInfos[i].getParameter(beanFactory, request, response);
			}
			response.write(invoke.invoke(args));
		}
	}
}
