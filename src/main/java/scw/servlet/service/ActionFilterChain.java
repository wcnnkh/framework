package scw.servlet.service;

import java.util.Collection;
import java.util.Iterator;

import scw.aop.Invoker;
import scw.core.utils.CollectionUtils;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;

public final class ActionFilterChain implements FilterChain {
	private Iterator<Filter> iterator;
	private final MethodParameter[] paramInfos;
	private final Invoker invoker;

	public ActionFilterChain(Invoker invoker, MethodParameter[] paramInfos, Collection<Filter> filterList) {
		this.paramInfos = paramInfos;
		this.invoker = invoker;
		if (!CollectionUtils.isEmpty(filterList)) {
			iterator = filterList.iterator();
		}
	}

	public void doFilter(Request request, Response response) throws Throwable {
		if (iterator == null) {
			invoke(request, response);
			return;
		}

		if (iterator.hasNext()) {
			iterator.next().doFilter(request, response, this);
		} else {
			invoke(request, response);
		}
	}

	private void invoke(Request request, Response response) throws Throwable {
		Object[] args = new Object[paramInfos.length];
		for (int i = 0; i < paramInfos.length; i++) {
			args[i] = paramInfos[i].getParameter(request, response);
		}
		response.write(invoker.invoke(args));
	}
}
