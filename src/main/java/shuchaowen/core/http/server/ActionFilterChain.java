package shuchaowen.core.http.server;

import java.util.Iterator;
import java.util.List;

import shuchaowen.core.invoke.Invoker;

public final class ActionFilterChain implements FilterChain {
	private Iterator<Filter> filterIterator;
	private Invoker invoke;
	private MethodParameter[] paramInfos;

	public ActionFilterChain(Invoker invoke, MethodParameter[] paramInfos, List<Filter> filterList) {
		this.invoke = invoke;
		this.paramInfos = paramInfos;
		if (filterList != null) {
			this.filterIterator = filterList.iterator();
		}
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
