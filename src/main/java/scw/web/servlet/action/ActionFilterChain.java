package scw.web.servlet.action;

import java.util.Iterator;
import java.util.List;

import scw.common.reflect.Invoker;
import scw.web.servlet.Request;
import scw.web.servlet.Response;

public final class ActionFilterChain implements FilterChain {
	private final Iterator<Filter> filterIterator;
	private final Invoker invoke;
	private final MethodParameter[] paramInfos;

	public ActionFilterChain(Invoker invoke, MethodParameter[] paramInfos, List<Filter> filterList) {
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
				args[i] = paramInfos[i].getParameter(request, response);
			}
			response.write(invoke.invoke(args));
		}
	}
}
